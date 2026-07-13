package com.sn.onepay.controller;

import com.sn.onepay.dto.ClientDTO;
import com.sn.onepay.services.ClientService;
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

@WebMvcTest(ClientController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClientControllerTest extends BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ClientService clientService;

    @Test
    void createClient_returns201() throws Exception {
        when(clientService.createClient(any())).thenReturn(mock(ClientDTO.class));

        mockMvc.perform(post("/v1/onepay/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstname\":\"John\",\"lastname\":\"Doe\",\"username\":\"jdoe\",\"email\":\"jdoe@mail.com\",\"role\":\"CLIENT\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void updateClient_returns200() throws Exception {
        when(clientService.updateClient(any(), anyLong())).thenReturn(mock(ClientDTO.class));

        mockMvc.perform(put("/v1/onepay/client/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstname\":\"John\",\"lastname\":\"Doe\",\"username\":\"jdoe\",\"email\":\"jdoe@mail.com\",\"role\":\"CLIENT\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void getClientByFilters_returns200() throws Exception {
        when(clientService.getClientsByFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/v1/onepay/client"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteClient_returns200() throws Exception {
        doNothing().when(clientService).deleteClient(anyLong());

        mockMvc.perform(delete("/v1/onepay/client/1"))
                .andExpect(status().isOk());
    }
}