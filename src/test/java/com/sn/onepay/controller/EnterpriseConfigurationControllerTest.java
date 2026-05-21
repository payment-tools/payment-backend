package com.sn.onepay.controller;

import com.sn.onepay.dto.EnterpriseConfigurationDTO;
import com.sn.onepay.services.EnterpriseConfigurationService;
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

@WebMvcTest(EnterpriseConfigurationController.class)
class EnterpriseConfigurationControllerTest extends BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    EnterpriseConfigurationService enterpriseConfigurationService;

    @Test
    void createEnterpriseConfiguration_returns201() throws Exception {
        when(enterpriseConfigurationService.createEnterpriseConfiguration(any())).thenReturn(mock(EnterpriseConfigurationDTO.class));

        mockMvc.perform(post("/v1/onepay/enterpriseConfiguration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"enterprisePercentage\":60,\"employeePercentage\":40}"))
                .andExpect(status().isCreated());
    }

    @Test
    void updateEnterpriseConfiguration_returns200() throws Exception {
        when(enterpriseConfigurationService.updateEnterpriseConfiguration(any(), anyLong())).thenReturn(mock(EnterpriseConfigurationDTO.class));

        mockMvc.perform(put("/v1/onepay/enterpriseConfiguration/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"enterprisePercentage\":60,\"employeePercentage\":40}"))
                .andExpect(status().isOk());
    }

    @Test
    void getEnterpriseConfigurationByFilters_returns200() throws Exception {
        when(enterpriseConfigurationService.getEnterpriseConfigurationsByFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/v1/onepay/enterpriseConfiguration"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteEnterpriseConfiguration_returns200() throws Exception {
        doNothing().when(enterpriseConfigurationService).deleteEnterpriseConfiguration(anyLong());

        mockMvc.perform(delete("/v1/onepay/enterpriseConfiguration/1"))
                .andExpect(status().isOk());
    }
}