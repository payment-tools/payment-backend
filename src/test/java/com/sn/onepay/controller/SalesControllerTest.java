package com.sn.onepay.controller;

import com.sn.onepay.dto.SalesDTO;
import com.sn.onepay.services.SalesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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

@WebMvcTest(SalesController.class)
@AutoConfigureMockMvc(addFilters = false)
class SalesControllerTest extends BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SalesService salesService;

    @Test
    void createSales_returns201() throws Exception {
        when(salesService.createSales(any())).thenReturn(mock(SalesDTO.class));

        mockMvc.perform(post("/v1/onepay/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"RestaurantX\",\"type\":\"RESTAURATION\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void updateSales_returns200() throws Exception {
        when(salesService.updateSales(any(), anyLong())).thenReturn(mock(SalesDTO.class));

        mockMvc.perform(put("/v1/onepay/sales/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"RestaurantX\",\"type\":\"RESTAURATION\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void getSalesByFilters_returns200() throws Exception {
        when(salesService.getSalesByFilters(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/v1/onepay/sales"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteSales_returns200() throws Exception {
        doNothing().when(salesService).deleteSales(anyLong());

        mockMvc.perform(delete("/v1/onepay/sales/1"))
                .andExpect(status().isOk());
    }
}