package com.sn.onepay.controller;

import com.sn.onepay.dto.EnterpriseProfileDTO;
import com.sn.onepay.services.EnterpriseProfileService;
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

@WebMvcTest(EnterpriseProfileController.class)
class EnterpriseProfileControllerTest extends BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    EnterpriseProfileService enterpriseProfileService;

    @Test
    void createEnterpriseProfile_returns201() throws Exception {
        when(enterpriseProfileService.createEnterpriseProfile(any())).thenReturn(mock(EnterpriseProfileDTO.class));

        mockMvc.perform(post("/v1/onepay/enterpriseProfile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstname\":\"John\",\"lastname\":\"Doe\",\"username\":\"jdoe\",\"role\":\"ENTERPRISE_ADMIN\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void updateEnterpriseProfile_returns200() throws Exception {
        when(enterpriseProfileService.updateEnterpriseProfile(any(), anyLong())).thenReturn(mock(EnterpriseProfileDTO.class));

        mockMvc.perform(put("/v1/onepay/enterpriseProfile/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstname\":\"John\",\"lastname\":\"Doe\",\"username\":\"jdoe\",\"role\":\"ENTERPRISE_ADMIN\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void getEnterpriseProfileByFilters_returns200() throws Exception {
        when(enterpriseProfileService.getEnterpriseProfilesByFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/v1/onepay/enterpriseProfile"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteEnterpriseProfile_returns200() throws Exception {
        doNothing().when(enterpriseProfileService).deleteEnterpriseProfile(anyLong());

        mockMvc.perform(delete("/v1/onepay/enterpriseProfile/1"))
                .andExpect(status().isOk());
    }
}