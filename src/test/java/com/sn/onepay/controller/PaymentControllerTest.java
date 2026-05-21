package com.sn.onepay.controller;

import com.sn.onepay.dto.PaymentDTO;
import com.sn.onepay.services.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest extends BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PaymentService paymentService;

    @Test
    void createPayment_returns201() throws Exception {
        when(paymentService.createPayment(any())).thenReturn(mock(PaymentDTO.class));

        mockMvc.perform(post("/v1/onepay/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"client\":{\"id\":1},\"cashier\":{\"id\":2},\"amount\":50.0,\"status\":\"ACTIVE\",\"module\":\"RESTAURATION\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void updatePayment_returns200() throws Exception {
        when(paymentService.updatePayment(any(), anyLong())).thenReturn(mock(PaymentDTO.class));

        mockMvc.perform(put("/v1/onepay/payment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"client\":{\"id\":1},\"cashier\":{\"id\":2},\"amount\":50.0,\"status\":\"ACTIVE\",\"module\":\"RESTAURATION\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void getPaymentByFilters_returns200() throws Exception {
        when(paymentService.getPaymentByFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/v1/onepay/payment"))
                .andExpect(status().isOk());
    }

    @Test
    void getSumOfPaymentsByClient_returns200() throws Exception {
        when(paymentService.getSumOfAllPaymentsByClientId(anyLong())).thenReturn(150.0);

        mockMvc.perform(get("/v1/onepay/payment/sum/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deletePayment_returns200() throws Exception {
        doNothing().when(paymentService).deletePayment(anyLong());

        mockMvc.perform(delete("/v1/onepay/payment/1"))
                .andExpect(status().isOk());
    }
}