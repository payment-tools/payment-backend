package com.sn.onepay.controller;

import com.sn.onepay.dto.SubventionDTO;
import com.sn.onepay.services.SubventionService;
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

@WebMvcTest(SubventionController.class)
@AutoConfigureMockMvc(addFilters = false)
class SubventionControllerTest extends BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SubventionService subventionService;

    @Test
    void createSubvention_returns201() throws Exception {
        when(subventionService.createSubvention(any())).thenReturn(mock(SubventionDTO.class));

        mockMvc.perform(post("/v1/onepay/subvention")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeePercent\":60.0,\"employerPercent\":40.0,\"partnershipId\":1,\"employeeGroupId\":2}"))
                .andExpect(status().isCreated());
    }

    @Test
    void createSubvention_withMissingRequiredFields_returns400() throws Exception {
        mockMvc.perform(post("/v1/onepay/subvention")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeePercent\":60.0,\"employerPercent\":40.0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSubvention_returns200() throws Exception {
        when(subventionService.updateSubvention(any(), anyLong())).thenReturn(mock(SubventionDTO.class));

        mockMvc.perform(put("/v1/onepay/subvention/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeePercent\":70.0,\"employerPercent\":30.0,\"active\":true}"))
                .andExpect(status().isOk());
    }

    @Test
    void getSubventionsByFilters_returns200() throws Exception {
        when(subventionService.getSubventionsByFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/v1/onepay/subvention"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteSubvention_returns200() throws Exception {
        doNothing().when(subventionService).deleteSubvention(anyLong());

        mockMvc.perform(delete("/v1/onepay/subvention/1"))
                .andExpect(status().isOk());
    }
}