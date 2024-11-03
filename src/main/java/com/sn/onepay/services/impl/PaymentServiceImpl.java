package com.sn.onepay.services.impl;

import com.sn.onepay.dto.PaymentDTO;
import com.sn.onepay.services.PaymentService;
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
    @Override
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        return null;
    }

    @Override
    public PaymentDTO updatePayment(PaymentDTO paymentDTO, Long paymentId) {
        return null;
    }

    @Override
    public void deletePayment(Long id) {

    }

    @Override
    public Page<PaymentDTO> getPaymentsByFilter(PaymentDTO paymentDTO, Pageable pageable) {
        return null;
    }
}
