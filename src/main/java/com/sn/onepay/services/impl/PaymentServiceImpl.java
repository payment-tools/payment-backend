package com.sn.onepay.services.impl;

import com.sn.onepay.dto.EnterpriseConfigurationDTO;
import com.sn.onepay.dto.PaymentDTO;
import com.sn.onepay.dto.SalesConfigurationsDTO;
import com.sn.onepay.entity.Payment;
import com.sn.onepay.enumeration.Modules;
import com.sn.onepay.enumeration.StateStatus;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.PaymentMapper;
import com.sn.onepay.repository.PaymentRepository;
import com.sn.onepay.services.EnterpriseConfigurationService;
import com.sn.onepay.services.PaymentService;
import com.sn.onepay.services.SalesConfigurationsService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    @Override
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Payment savedPayment = new Payment();

        Long clientId = paymentDTO.client().getId();
        Modules module = paymentDTO.cashier().getSales().getType();

        /*Get sales configuration*/
        SalesConfigurationsDTO salesConfigurations = salesConfigurationsService.getSalesConfigurationsBySalesId(paymentDTO.partnership().sales().id());

        /* Get enterprise max amount configuration*/
        EnterpriseConfigurationDTO enterpriseConfiguration = enterpriseConfigurationService.getEnterpriseConfigurationByEnterpriseId(paymentDTO.partnership().enterprise().id());

        if(salesConfigurations.maxAmount() >= paymentDTO.amount() && salesConfigurations.minAmount() <= paymentDTO.amount()) {
            switch (module) {
                case RESTAURATION -> {
                    if (paymentDTO.amount() + getSumOfAllPaymentsByClientIdAndModule(clientId, module) > enterpriseConfiguration.maxAmountRestauration()) {
                        throw new ObjectValidationException("Montant dépassé");
                    } else {
                        savedPayment = paymentRepository.save(paymentMapper.asEntity(paymentDTO));
                    }
                }
                case MARKET -> {
                    if (paymentDTO.amount() + getSumOfAllPaymentsByClientIdAndModule(clientId, module) > enterpriseConfiguration.maxAmountMarket()) {
                        throw new ObjectValidationException("Montant dépassé");
                    } else {
                        savedPayment = paymentRepository.save(paymentMapper.asEntity(paymentDTO));
                    }
                }
                case GAS_STATION -> {
                    if (paymentDTO.amount() + getSumOfAllPaymentsByClientIdAndModule(clientId, module) > enterpriseConfiguration.maxAmountGasStation()) {
                        throw new ObjectValidationException("Montant dépassé");
                    } else {
                        savedPayment = paymentRepository.save(paymentMapper.asEntity(paymentDTO));
                    }
                }
                case TELEPHONY -> {
                    if (paymentDTO.amount() + getSumOfAllPaymentsByClientIdAndModule(clientId, module) > enterpriseConfiguration.maxAmountTelephony()) {
                        throw new ObjectValidationException("Montant dépassé");
                    } else {
                        savedPayment = paymentRepository.save(paymentMapper.asEntity(paymentDTO));
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
        payment.setStatus(StateStatus.INACTIVE);

        paymentRepository.saveAndFlush(payment);

        log.info("Payment deleted: {}", paymentId);
        log.trace("Payment deleted with id: {}", paymentId);

    }

    @Override
    public double getSumOfAllPaymentsByClientIdAndModule(Long clientId, Modules module) {
        if(paymentRepository.getAllPaymentsByClientId(clientId).isEmpty())
            return 0;

        return paymentRepository.findAllPaymentsByClientIdAndModule(clientId, module.name());

    }

    @Override
    public Page<PaymentDTO> getPaymentsByFilter(PaymentDTO paymentDTO, Pageable pageable) {
        return null;
    }
}
