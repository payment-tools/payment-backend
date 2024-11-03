package com.sn.onepay.services.impl;

import com.sn.onepay.dto.PaymentDTO;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.PaymentMapper;
import com.sn.onepay.repository.PaymentRepository;
import com.sn.onepay.services.PaymentService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
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

    @Override
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {

        var savedPayment = paymentRepository.save(paymentMapper.asEntity(paymentDTO));

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

        paymentRepository.findById(paymentId).orElseThrow(() ->  new ResourceNotFoundException("Payment", "ID", paymentId));

        paymentRepository.deleteById(paymentId);

        log.info("Payment deleted: {}", paymentId);
        log.trace("Payment deleted with id: {}", paymentId);

    }

    @Override
    public Page<PaymentDTO> getPaymentsByFilter(PaymentDTO paymentDTO, Pageable pageable) {
        return null;
    }
}
