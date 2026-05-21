package com.sn.onepay.controller;

import com.sn.onepay.dto.EnterpriseDTO;
import com.sn.onepay.mapper.EnterpriseMapper;
import com.sn.onepay.repository.EnterpriseRepository;
import com.sn.onepay.services.EnterpriseService;
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

@WebMvcTest(EnterpriseController.class)
class EnterpriseControllerTest extends BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    EnterpriseService enterpriseService;

    @MockBean
    EnterpriseRepository enterpriseRepository;

    @MockBean
    EnterpriseMapper enterpriseMapper;

    @Test
    void createEnterprise_returns201() throws Exception {
        when(enterpriseService.createEnterprise(any())).thenReturn(mock(EnterpriseDTO.class));

        mockMvc.perform(post("/v1/onepay/enterprise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test\",\"maxQuota\":100}"))
                .andExpect(status().isCreated());
    }

    @Test
    void updateEnterprise_returns200() throws Exception {
        when(enterpriseService.updateEnterprise(any(), anyLong())).thenReturn(mock(EnterpriseDTO.class));

        mockMvc.perform(put("/v1/onepay/enterprise/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated\",\"maxQuota\":100}"))
                .andExpect(status().isOk());
    }

    @Test
    void getEnterpriseByFilters_returns200() throws Exception {
        when(enterpriseService.getEnterprisesByFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/v1/onepay/enterprise"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteEnterprise_returns200() throws Exception {
        doNothing().when(enterpriseService).deleteEnterprise(anyLong());

        mockMvc.perform(delete("/v1/onepay/enterprise/1"))
                .andExpect(status().isOk());
    }
}