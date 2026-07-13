package com.sn.onepay.controller;

import com.sn.onepay.dto.SalesConfigurationsDTO;
import com.sn.onepay.services.SalesConfigurationsService;
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

@WebMvcTest(SalesConfigurationsController.class)
@AutoConfigureMockMvc(addFilters = false)
class SalesConfigurationsControllerTest extends BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SalesConfigurationsService salesConfigurationsService;

    @Test
    void createSalesConfiguration_returns201() throws Exception {
        when(salesConfigurationsService.createSalesConfigurations(any())).thenReturn(mock(SalesConfigurationsDTO.class));

        mockMvc.perform(post("/v1/onepay/salesConfiguration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"minAmount\":10.0,\"maxAmount\":100.0}"))
                .andExpect(status().isCreated());
    }

    @Test
    void updateSalesConfigurations_returns200() throws Exception {
        when(salesConfigurationsService.updateSalesConfigurations(any(), anyLong())).thenReturn(mock(SalesConfigurationsDTO.class));

        mockMvc.perform(put("/v1/onepay/salesConfiguration/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"minAmount\":10.0,\"maxAmount\":100.0}"))
                .andExpect(status().isOk());
    }

    @Test
    void getSalesConfigurationsByFilters_returns200() throws Exception {
        when(salesConfigurationsService.getSalesConfigurationsByFilters(any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/v1/onepay/salesConfiguration"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteSalesConfigurations_returns200() throws Exception {
        doNothing().when(salesConfigurationsService).deleteSalesConfigurations(anyLong());

        mockMvc.perform(delete("/v1/onepay/salesConfiguration/1"))
                .andExpect(status().isOk());
    }
}