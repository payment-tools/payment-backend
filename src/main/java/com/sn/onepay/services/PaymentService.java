package com.sn.onepay.services;

import com.sn.onepay.dto.PaymentDTO;
import com.sn.onepay.enumeration.Modules;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface PaymentService {

    PaymentDTO createPayment(PaymentDTO paymentDTO);

    PaymentDTO updatePayment(PaymentDTO paymentDTO, Long paymentId);

    void deletePayment(Long paymentId);

    double getSumOfAllPaymentsByClientIdAndModule(Long clientId, Modules module);

    double getSumOfAllPaymentsByClientId(Long clientId);

    Page<PaymentDTO> getPaymentByFilters(Long id, String ref, Long clientId, Long cashierId, Double amount, Boolean active, Modules module, LocalDateTime paymentDate, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);
}
