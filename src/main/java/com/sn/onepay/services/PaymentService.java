package com.sn.onepay.services;

import com.sn.onepay.dto.PaymentDTO;
import com.sn.onepay.enumeration.Modules;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentService {

    PaymentDTO createPayment(PaymentDTO paymentDTO);

    PaymentDTO updatePayment(PaymentDTO paymentDTO, Long paymentId);

    void deletePayment(Long paymentId);

    double getSumOfAllPaymentsByClientIdAndModule(Long clientId, Modules module);

    Page<PaymentDTO> getPaymentsByFilter(PaymentDTO paymentDTO, Pageable pageable);
}
