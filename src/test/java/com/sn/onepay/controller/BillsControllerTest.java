package com.sn.onepay.controller;

import com.sn.onepay.dto.BillsDTO;
import com.sn.onepay.services.BillsService;
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

@WebMvcTest(BillsController.class)
@AutoConfigureMockMvc(addFilters = false)
class BillsControllerTest extends BaseControllerTest {

    static final String VALID_BODY = "{\"partnership\":{\"id\":1,\"sales\":{\"id\":1,\"type\":\"RESTAURATION\"},\"enterprise\":{\"id\":2,\"name\":\"Corp\",\"maxQuota\":100}}," +
            "\"startDate\":\"2026-07-01T00:00:00\",\"endDate\":\"2026-07-31T23:59:59\"," +
            "\"totalAmount\":5000.0,\"billStatus\":\"UNPAYED\",\"period\":\"2026-07\"}";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BillsService billsService;

    @Test
    void createBills_returns201() throws Exception {
        when(billsService.createBills(any())).thenReturn(mock(BillsDTO.class));

        mockMvc.perform(post("/v1/onepay/bills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated());
    }

    @Test
    void createBills_returns400WhenBodyInvalid() throws Exception {
        mockMvc.perform(post("/v1/onepay/bills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBills_returns200() throws Exception {
        when(billsService.updateBills(any(), anyLong())).thenReturn(mock(BillsDTO.class));

        mockMvc.perform(put("/v1/onepay/bills/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk());
    }

    @Test
    void getBillsByFilters_returns200() throws Exception {
        when(billsService.getBillsByFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/v1/onepay/bills"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBills_returns200() throws Exception {
        doNothing().when(billsService).deleteBills(anyLong());

        mockMvc.perform(delete("/v1/onepay/bills/1"))
                .andExpect(status().isOk());
    }
}
