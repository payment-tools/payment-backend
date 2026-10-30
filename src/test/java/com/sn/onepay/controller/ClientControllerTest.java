package com.sn.onepay.controller;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.mock.web.MockMultipartFile;
import com.sn.onepay.services.ClientImportService;
import com.sn.onepay.exceptions.InvalidFileException;
import com.sn.onepay.dto.ClientImportReportDTO;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.mockito.ArgumentMatchers.eq;
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

    @MockBean
    ClientImportService clientImportService;

    @Test
    void createClient_returns201() throws Exception {
        when(clientService.createClient(any())).thenReturn(mock(ClientDTO.class));

        mockMvc.perform(post("/v1/onepay/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstname\":\"John\",\"lastname\":\"Doe\",\"username\":\"jdoe\",\"email\":\"jdoe@mail.com\",\"phoneNumber\":\"+221 77 123 45 67\",\"role\":\"CLIENT\",\"enterpriseId\":1}"))
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
    void getClientById_returns200() throws Exception {
        when(clientService.getClientById(1L)).thenReturn(mock(ClientDTO.class));

        mockMvc.perform(get("/v1/onepay/client/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteClient_returns200() throws Exception {
        doNothing().when(clientService).deleteClient(anyLong());

        mockMvc.perform(delete("/v1/onepay/client/1"))
                .andExpect(status().isOk());
    }

    @Test
    void createClient_returns400WhenPhoneNumberMissing() throws Exception {
        mockMvc.perform(post("/v1/onepay/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstname\":\"John\",\"lastname\":\"Doe\",\"username\":\"jdoe\",\"email\":\"jdoe@mail.com\",\"role\":\"CLIENT\",\"enterpriseId\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createClient_returns400WhenPhoneNumberNotSenegalese() throws Exception {
        mockMvc.perform(post("/v1/onepay/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstname\":\"John\",\"lastname\":\"Doe\",\"username\":\"jdoe\",\"email\":\"jdoe@mail.com\",\"phoneNumber\":\"12345\",\"role\":\"CLIENT\",\"enterpriseId\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateClient_returns400WhenPhoneNumberNotSenegalese() throws Exception {
        mockMvc.perform(put("/v1/onepay/client/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phoneNumber\":\"12345\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void importClients_superAdmin_passesEnterpriseAndRole() throws Exception {
        var report = new ClientImportReportDTO(1, List.of(new ClientImportReportDTO.CreatedRow(2, 10L, "awa.ndiaye")), List.of());
        when(clientImportService.importClients(any(), eq(9L), eq("super.admin"), eq(true))).thenReturn(report);
        var file = new MockMultipartFile("file", "employes.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[]{1});

        mockMvc.perform(multipart("/v1/onepay/client/import").file(file).param("enterpriseId", "9")
                        .principal(authentication("super.admin", "ROLE_SUPER_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.created[0].username").value("awa.ndiaye"));
    }

    @Test
    void importClients_enterpriseAdmin_isNotSuperAdmin() throws Exception {
        when(clientImportService.importClients(any(), eq(null), eq("enterprise.admin"), eq(false)))
                .thenReturn(new ClientImportReportDTO(0, List.of(), List.of()));
        var file = new MockMultipartFile("file", "employes.xlsx", "application/octet-stream", new byte[]{1});

        mockMvc.perform(multipart("/v1/onepay/client/import").file(file)
                        .principal(authentication("enterprise.admin", "ROLE_ENTERPRISE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRows").value(0));
    }

    @Test
    void importClients_withoutAuthentication_isNotSuperAdmin() throws Exception {
        when(clientImportService.importClients(any(), eq(null), eq(null), eq(false)))
                .thenReturn(new ClientImportReportDTO(0, List.of(), List.of()));
        var file = new MockMultipartFile("file", "employes.xlsx", "application/octet-stream", new byte[]{1});

        mockMvc.perform(multipart("/v1/onepay/client/import").file(file))
                .andExpect(status().isOk());
    }

    @Test
    void importClients_returns400ForInvalidFile() throws Exception {
        when(clientImportService.importClients(any(), any(), any(), eq(false)))
                .thenThrow(new InvalidFileException("Fichier Excel (.xlsx) illisible"));
        var file = new MockMultipartFile("file", "employes.csv", "text/csv", new byte[]{1});

        mockMvc.perform(multipart("/v1/onepay/client/import").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Fichier Excel (.xlsx) illisible"));
    }

    private static JwtAuthenticationToken authentication(String username, String role) {
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("preferred_username", username).build();
        return new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority(role)));
    }
}
