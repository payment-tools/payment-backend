package com.sn.onepay.controller;

import com.sn.onepay.dto.FirstPasswordChangeDTO;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.keycloak.KeycloakUnavailableException;
import com.sn.onepay.services.PasswordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PasswordController.class)
@AutoConfigureMockMvc(addFilters = false)
class PasswordControllerTest extends BaseControllerTest {

    static final String BODY = "{\"username\":\"cashier.test\",\"temporaryPassword\":\"Temp123!\",\"newPassword\":\"Nouveau123!\"}";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PasswordService passwordService;

    @Test
    void changeTemporaryPassword_returns204() throws Exception {
        mockMvc.perform(post("/v1/onepay/auth/first-password").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isNoContent());

        verify(passwordService).changeTemporaryPassword(new FirstPasswordChangeDTO("cashier.test", "Temp123!", "Nouveau123!"));
    }

    @Test
    void newPasswordTooShort_returns400() throws Exception {
        mockMvc.perform(post("/v1/onepay/auth/first-password").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"cashier.test\",\"temporaryPassword\":\"Temp123!\",\"newPassword\":\"court\"}"))
                .andExpect(status().isBadRequest());

        verify(passwordService, never()).changeTemporaryPassword(any());
    }

    @Test
    void businessRejection_returns422() throws Exception {
        doThrow(new ObjectValidationException("Identifiant ou mot de passe temporaire incorrect."))
                .when(passwordService).changeTemporaryPassword(any());

        mockMvc.perform(post("/v1/onepay/auth/first-password").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Identifiant ou mot de passe temporaire incorrect."));
    }

    @Test
    void keycloakUnavailable_returns502() throws Exception {
        doThrow(new KeycloakUnavailableException("Service d'authentification indisponible", null))
                .when(passwordService).changeTemporaryPassword(any());

        mockMvc.perform(post("/v1/onepay/auth/first-password").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.message").value("Service d'authentification indisponible"));
    }
}
