package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.EnterpriseConfigurationDTO;
import com.sn.onepay.dto.PartnershipDTO;
import com.sn.onepay.dto.PaymentDTO;
import com.sn.onepay.dto.SalesConfigurationsDTO;
import com.sn.onepay.entity.EmployeeGroup;
import com.sn.onepay.entity.Payment;
import com.sn.onepay.entity.QPayment;
import com.sn.onepay.entity.Subvention;
import com.sn.onepay.enumeration.Modules;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.PaymentMapper;
import com.sn.onepay.repository.EmployeeGroupRepository;
import com.sn.onepay.repository.PaymentRepository;
import com.sn.onepay.repository.SubventionRepository;
import com.sn.onepay.services.EnterpriseConfigurationService;
import com.sn.onepay.services.PartnershipService;
import com.sn.onepay.services.PaymentService;
import com.sn.onepay.services.QRCodeService;
import com.sn.onepay.services.SalesConfigurationsService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    final PaymentRepository paymentRepository;
    final PaymentMapper paymentMapper;
    final EnterpriseConfigurationService enterpriseConfigurationService;
    final SalesConfigurationsService salesConfigurationsService;
    final PartnershipService partnershipService;
    final QRCodeService qrCodeService;
    final EmployeeGroupRepository employeeGroupRepository;
    final SubventionRepository subventionRepository;

    @Override
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {

        Long clientId = paymentDTO.client().id();
        Modules module = paymentDTO.cashier().sales().type();

        /*Get sales configuration*/
        SalesConfigurationsDTO salesConfigurations = salesConfigurationsService.getSalesConfigurationsBySalesId(paymentDTO.cashier().sales().id());

        /* Get enterprise max amount configuration*/
        EnterpriseConfigurationDTO enterpriseConfiguration = enterpriseConfigurationService.getEnterpriseConfigurationByEnterpriseId(paymentDTO.client().enterprise().id());

        /*Check if partnership exist*/
        PartnershipDTO partnership = partnershipService.getPartnershipsBySalesIdAndEnterpriseId(paymentDTO.cashier().sales().id(), paymentDTO.client().enterprise().id());
        if (Objects.isNull(partnership))
            throw new ObjectValidationException("Paiement non autorisé");

        if (!Boolean.TRUE.equals(partnership.active()) || salesConfigurations.maxAmount() < paymentDTO.amount() || salesConfigurations.minAmount() > paymentDTO.amount())
            throw new ObjectValidationException("Paiement non autorisé");

        /*Consume the client QR code if the payment comes from a cashier scan (mode B)*/
        if (Objects.nonNull(paymentDTO.qrCodeRef()))
            qrCodeService.consumeClientQrCode(paymentDTO.qrCodeRef(), clientId);

        if (paymentDTO.amount() + getSumOfAllPaymentsByClientIdAndModule(clientId, module) > getMaxAmountForModule(enterpriseConfiguration, module))
            throw new ObjectValidationException("Montant dépassé");

        /*Resolve the subvention covering this client for this partnership and apply the employer/employee split*/
        Subvention subvention = resolveApplicableSubvention(clientId, partnership.id());

        Payment payment = paymentMapper.asEntity(paymentDTO);
        payment.setActive(true);
        payment.setEmployerAmount(paymentDTO.amount() * subvention.getEmployerPercent() / 100);
        payment.setEmployeeAmount(paymentDTO.amount() * subvention.getEmployeePercent() / 100);

        Payment savedPayment = paymentRepository.save(payment);

        log.info("Payment saved: {}", savedPayment);
        log.trace("Payment saved with id: {}", savedPayment.getId());

        return paymentMapper.asDTO(savedPayment);
    }

    private double getMaxAmountForModule(EnterpriseConfigurationDTO enterpriseConfiguration, Modules module) {
        return switch (module) {
            case RESTAURATION -> enterpriseConfiguration.maxAmountRestauration();
            case MARKET -> enterpriseConfiguration.maxAmountMarket();
            case GAS_STATION -> enterpriseConfiguration.maxAmountGasStation();
            case TELEPHONY -> enterpriseConfiguration.maxAmountTelephony();
        };
    }

    private Subvention resolveApplicableSubvention(Long clientId, Long partnershipId) {

        List<Long> employeeGroupIds = employeeGroupRepository.findByActiveTrueAndClients_Id(clientId).stream()
                .map(EmployeeGroup::getId)
                .toList();

        if (employeeGroupIds.isEmpty())
            throw new ObjectValidationException("Aucune subvention active pour ce client");

        return subventionRepository.findByActiveTrueAndPartnership_IdAndEmployeeGroup_IdIn(partnershipId, employeeGroupIds).stream()
                .max(Comparator.comparingDouble(Subvention::getEmployerPercent))
                .orElseThrow(() -> new ObjectValidationException("Aucune subvention active pour ce client"));
    }

    @Override
    public PaymentDTO updatePayment(PaymentDTO paymentDTO, Long paymentId) {

        paymentRepository.findById(paymentId).orElseThrow(() ->  new ResourceNotFoundException("Payment", "ID", paymentId));

        var updatedPayment = paymentRepository.saveAndFlush(paymentMapper.asEntity(paymentDTO));

        log.info("Payment updated: {}", updatedPayment);
        log.trace("Payment updated with id: {}", updatedPayment.getId());

        return paymentMapper.asDTO(updatedPayment);
    }

    @Override
    public void deletePayment(Long paymentId) {

        /*Implements logical deletion*/
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() ->  new ResourceNotFoundException("Payment", "ID", paymentId));
        payment.setActive(false);
        paymentRepository.saveAndFlush(payment);

        log.info("Payment deleted: {}", paymentId);
        log.trace("Payment deleted with id: {}", paymentId);

    }

    @Override
    public double getSumOfAllPaymentsByClientIdAndModule(Long clientId, Modules module) {
        Double sum = paymentRepository.findAllPaymentsByClientIdAndModule(clientId, module.name());
        return sum != null ? sum : 0d;
    }

    @Override
    public double getSumOfAllPaymentsByClientId(Long clientId) {
        Double sum = paymentRepository.findSumOfAllActivePaymentsByClientId(clientId);
        return sum != null ? sum : 0d;
    }

    @Override
    public Page<PaymentDTO> getPaymentByFilters(Long id, String ref, Long clientId, Long cashierId, Double amount, Boolean active, Modules module, LocalDateTime paymentDate, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable) {

        QPayment payment = QPayment.payment;
        BooleanBuilder builder = new BooleanBuilder();

        if (id != null) {
            builder.and(payment.id.eq(id));
        }
        if (ref != null && !ref.isEmpty()) {
            builder.and(payment.ref.containsIgnoreCase(ref));
        }
        if (clientId != null) {
            builder.and(payment.client.id.eq(clientId));
        }
        if (cashierId != null) {
            builder.and(payment.cashier.id.eq(cashierId));
        }
        if (amount != null) {
            builder.and(payment.amount.eq(amount));
        }
        if (active != null) {
            builder.and(payment.active.eq(active));
        }
        if (module != null) {
            builder.and(payment.module.eq(module));
        }
        if (paymentDate != null) {
            builder.and(payment.paymentDate.eq(paymentDate));
        }
        if (creationDate != null) {
            builder.and(payment.creationDate.goe(creationDate));
        }
        if (modificationDate != null) {
            builder.and(payment.modificationDate.loe(modificationDate));
        }

        Page<Payment> result = paymentRepository.findAll(builder, pageable);

        return result.map(paymentMapper::asDTO);
    }
}