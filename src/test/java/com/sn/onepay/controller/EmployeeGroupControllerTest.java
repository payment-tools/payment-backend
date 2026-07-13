package com.sn.onepay.controller;

import com.sn.onepay.dto.EmployeeGroupDTO;
import com.sn.onepay.services.EmployeeGroupService;
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

@WebMvcTest(EmployeeGroupController.class)
class EmployeeGroupControllerTest extends BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    EmployeeGroupService employeeGroupService;

    @Test
    void createEmployeeGroup_returns201() throws Exception {
        when(employeeGroupService.createEmployeeGroup(any())).thenReturn(mock(EmployeeGroupDTO.class));

        mockMvc.perform(post("/v1/onepay/employee-group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"RH\",\"enterpriseId\":1,\"clientIds\":[1,2]}"))
                .andExpect(status().isCreated());
    }

    @Test
    void createEmployeeGroup_withMissingRequiredFields_returns400() throws Exception {
        mockMvc.perform(post("/v1/onepay/employee-group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"RH\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEmployeeGroup_returns200() throws Exception {
        when(employeeGroupService.updateEmployeeGroup(any(), anyLong())).thenReturn(mock(EmployeeGroupDTO.class));

        mockMvc.perform(put("/v1/onepay/employee-group/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"RH Updated\",\"clientIds\":[3],\"active\":true}"))
                .andExpect(status().isOk());
    }

    @Test
    void getEmployeeGroupsByFilters_returns200() throws Exception {
        when(employeeGroupService.getEmployeeGroupsByFilters(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/v1/onepay/employee-group"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteEmployeeGroup_returns200() throws Exception {
        doNothing().when(employeeGroupService).deleteEmployeeGroup(anyLong());

        mockMvc.perform(delete("/v1/onepay/employee-group/1"))
                .andExpect(status().isOk());
    }
}