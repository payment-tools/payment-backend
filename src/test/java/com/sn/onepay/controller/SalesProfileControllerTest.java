package com.sn.onepay.controller;

import com.sn.onepay.dto.SalesProfileDTO;
import com.sn.onepay.services.SalesProfileService;
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

@WebMvcTest(SalesProfileController.class)
class SalesProfileControllerTest extends BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SalesProfileService salesProfileService;

    @Test
    void createSalesProfile_returns201() throws Exception {
        when(salesProfileService.createSalesProfile(any())).thenReturn(mock(SalesProfileDTO.class));

        mockMvc.perform(post("/v1/onepay/salesProfile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstname\":\"Jean\",\"lastname\":\"Dupont\",\"username\":\"jdup\",\"role\":\"SALES_ADMIN\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void updateSalesProfile_returns200() throws Exception {
        when(salesProfileService.updateSalesProfile(any(), anyLong())).thenReturn(mock(SalesProfileDTO.class));

        mockMvc.perform(put("/v1/onepay/salesProfile/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstname\":\"Jean\",\"lastname\":\"Dupont\",\"username\":\"jdup\",\"role\":\"SALES_ADMIN\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void getSalesProfileByFilters_returns200() throws Exception {
        when(salesProfileService.getSalesProfilesByFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/v1/onepay/salesProfile"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteSalesProfile_returns200() throws Exception {
        doNothing().when(salesProfileService).deleteSalesProfile(anyLong());

        mockMvc.perform(delete("/v1/onepay/salesProfile/1"))
                .andExpect(status().isOk());
    }
}