package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.EnterpriseConfigurationDTO;
import com.sn.onepay.dto.PartnershipDTO;
import com.sn.onepay.dto.PaymentDTO;
import com.sn.onepay.dto.SalesConfigurationsDTO;
import com.sn.onepay.entity.Payment;
import com.sn.onepay.entity.QPayment;
import com.sn.onepay.enumeration.Modules;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.PaymentMapper;
import com.sn.onepay.repository.PaymentRepository;
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

    @Override
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Payment savedPayment = new Payment();

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

        if(Boolean.TRUE.equals(partnership.active()) && (salesConfigurations.maxAmount() >= paymentDTO.amount() && salesConfigurations.minAmount() <= paymentDTO.amount())) {
            /*Consume the client QR code if the payment comes from a cashier scan (mode B)*/
            if (Objects.nonNull(paymentDTO.qrCodeRef()))
                qrCodeService.consumeClientQrCode(paymentDTO.qrCodeRef(), clientId);

            switch (module) {
                case RESTAURATION -> {
                    if (paymentDTO.amount() + getSumOfAllPaymentsByClientIdAndModule(clientId, module) > enterpriseConfiguration.maxAmountRestauration()) {
                        throw new ObjectValidationException("Montant dépassé");
                    } else {
                        savedPayment = paymentRepository.save(paymentMapper.asEntity(paymentDTO));
                        savedPayment.setActive(true);
                        savedPayment = paymentRepository.save(savedPayment);
                    }
                }
                case MARKET -> {
                    if (paymentDTO.amount() + getSumOfAllPaymentsByClientIdAndModule(clientId, module) > enterpriseConfiguration.maxAmountMarket()) {
                        throw new ObjectValidationException("Montant dépassé");
                    } else {
                        savedPayment = paymentMapper.asEntity(paymentDTO);
                        savedPayment.setActive(true);
                        savedPayment = paymentRepository.save(savedPayment);
                    }
                }
                case GAS_STATION -> {
                    if (paymentDTO.amount() + getSumOfAllPaymentsByClientIdAndModule(clientId, module) > enterpriseConfiguration.maxAmountGasStation()) {
                        throw new ObjectValidationException("Montant dépassé");
                    } else {
                        savedPayment = paymentMapper.asEntity(paymentDTO);
                        savedPayment.setActive(true);
                        savedPayment = paymentRepository.save(savedPayment);
                    }
                }
                case TELEPHONY -> {
                    if (paymentDTO.amount() + getSumOfAllPaymentsByClientIdAndModule(clientId, module) > enterpriseConfiguration.maxAmountTelephony()) {
                        throw new ObjectValidationException("Montant dépassé");
                    } else {
                        savedPayment = paymentMapper.asEntity(paymentDTO);
                        savedPayment.setActive(true);
                        savedPayment = paymentRepository.save(savedPayment);
                    }
                }
            }
        } else {
            throw new ObjectValidationException("Paiement non autorisé");
        }

        log.info("Payment saved: {}", savedPayment);
        log.trace("Payment saved with id: {}", savedPayment.getId());

        return paymentMapper.asDTO(savedPayment);
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