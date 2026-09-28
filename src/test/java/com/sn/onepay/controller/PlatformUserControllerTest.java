package com.sn.onepay.controller;

import com.sn.onepay.dto.PlatformUserDTO;
import com.sn.onepay.enumeration.PlatformUserType;
import com.sn.onepay.services.PlatformUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlatformUserController.class)
@AutoConfigureMockMvc(addFilters = false)
class PlatformUserControllerTest extends BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PlatformUserService platformUserService;

    @Test
    void getPlatformUsersByFilters_returns200() throws Exception {
        when(platformUserService.getPlatformUsersByFilters(any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/v1/onepay/platformUser"))
                .andExpect(status().isOk());
    }

    @Test
    void getPlatformUserByTypeAndId_returns200() throws Exception {
        when(platformUserService.getPlatformUserByTypeAndId(eq(PlatformUserType.CLIENT), eq(1L)))
                .thenReturn(mock(PlatformUserDTO.class));

        mockMvc.perform(get("/v1/onepay/platformUser/CLIENT/1"))
                .andExpect(status().isOk());
    }
}
