package com.sn.onepay.controller;

import com.sn.onepay.dto.AuditLogDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*AuditLogService is already @MockBean-declared on BaseControllerTest (needed by every slice
  because of AuditLoggingInterceptor/WebMvcConfig) - reused here as the controller's own dependency*/
@WebMvcTest(AuditLogController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuditLogControllerTest extends BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getAuditLogsByFilters_returns200() throws Exception {
        when(auditLogService.getAuditLogsByFilters(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/v1/onepay/auditLog"))
                .andExpect(status().isOk());
    }

    @Test
    void getAuditLogById_returns200() throws Exception {
        when(auditLogService.getAuditLogById(1L)).thenReturn(mock(AuditLogDTO.class));

        mockMvc.perform(get("/v1/onepay/auditLog/1"))
                .andExpect(status().isOk());
    }
}
