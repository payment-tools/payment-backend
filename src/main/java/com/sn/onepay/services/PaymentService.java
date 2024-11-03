package com.sn.onepay.services;

import com.sn.onepay.dto.PaymentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    PaymentDTO createPayment(PaymentDTO paymentDTO);

    PaymentDTO updatePayment(PaymentDTO paymentDTO, Long paymentId);

    void deletePayment(Long id);

    Page<PaymentDTO> getPaymentsByFilter(PaymentDTO paymentDTO, Pageable pageable);
}
