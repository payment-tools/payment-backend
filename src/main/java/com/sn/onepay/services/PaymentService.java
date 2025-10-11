package com.sn.onepay.services;

import com.sn.onepay.dto.PaymentDTO;
import com.sn.onepay.entity.Cashier;
import com.sn.onepay.entity.Client;
import com.sn.onepay.enumeration.Modules;
import com.sn.onepay.enumeration.StateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {

    PaymentDTO createPayment(PaymentDTO paymentDTO);

    PaymentDTO updatePayment(PaymentDTO paymentDTO, Long paymentId);

    void deletePayment(Long paymentId);

    double getSumOfAllPaymentsByClientIdAndModule(Long clientId, Modules module);

    Page<PaymentDTO> getPaymentByFilters(Long id, String ref, Client client, Cashier cashier, Double amount, StateStatus status, Modules module, LocalDateTime paymentDate, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);
}
