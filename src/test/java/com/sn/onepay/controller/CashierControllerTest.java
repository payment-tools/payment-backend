package com.sn.onepay.controller;

import com.sn.onepay.dto.CashierDTO;
import com.sn.onepay.services.CashierService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(CashierController.class)
@AutoConfigureMockMvc(addFilters = false)
class CashierControllerTest extends BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CashierService cashierService;

    @Test
    void createCashier_returns201() throws Exception {
        when(cashierService.createCashier(any())).thenReturn(mock(CashierDTO.class));

        mockMvc.perform(post("/v1/onepay/cashier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstname\":\"Jean\",\"lastname\":\"Pierre\",\"username\":\"jp\",\"email\":\"jp@mail.com\",\"role\":\"CASHIER\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void updateCashier_returns200() throws Exception {
        when(cashierService.updateCashier(any(), anyLong())).thenReturn(mock(CashierDTO.class));

        mockMvc.perform(put("/v1/onepay/cashier/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstname\":\"Jean\",\"lastname\":\"Pierre\",\"username\":\"jp\",\"email\":\"jp@mail.com\",\"role\":\"CASHIER\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void getCashierByFilters_returns200() throws Exception {
        mockMvc.perform(get("/v1/onepay/cashier"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCashier_returns200() throws Exception {
        doNothing().when(cashierService).deleteCashier(anyLong());

        mockMvc.perform(delete("/v1/onepay/cashier/1"))
                .andExpect(status().isOk());
    }
}