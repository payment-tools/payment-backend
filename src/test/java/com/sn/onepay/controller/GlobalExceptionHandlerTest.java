package com.sn.onepay.controller;

import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceAlreadyExistException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.services.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest extends BaseControllerTest {

    static final String VALID_PAYMENT_BODY =
            "{\"client\":{\"id\":1},\"cashier\":{\"id\":2},\"amount\":50.0,\"module\":\"RESTAURATION\"}";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PaymentService paymentService;

    @Test
    void objectValidationException_returns422WithMessage() throws Exception {
        when(paymentService.createPayment(any()))
                .thenThrow(new ObjectValidationException("Paiement non autorisé"));

        mockMvc.perform(post("/v1/onepay/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYMENT_BODY))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Paiement non autorisé"));
    }

    @Test
    void resourceNotFoundException_returns404WithMessage() throws Exception {
        doThrow(new ResourceNotFoundException("Payment", "ID", 99L))
                .when(paymentService).deletePayment(anyLong());

        mockMvc.perform(delete("/v1/onepay/payment/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Payment not found with ID : 99"));
    }

    @Test
    void resourceAlreadyExistException_returns409WithMessage() throws Exception {
        when(paymentService.createPayment(any()))
                .thenThrow(new ResourceAlreadyExistException("Payment already exists"));

        mockMvc.perform(post("/v1/onepay/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYMENT_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Payment already exists"));
    }
}