package com.sn.onepay.keycloak;

import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.keycloak.KeycloakAdminClient.KeycloakUser;
import com.sn.onepay.keycloak.KeycloakAdminClient.PasswordCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class KeycloakAdminClientTest {

    static final String KC = "http://kc";
    static final String TOKEN_URL = KC + "/realms/onepay/protocol/openid-connect/token";
    static final String USERS_URL = KC + "/admin/realms/onepay/users";

    MockRestServiceServer server;
    KeycloakAdminClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new KeycloakAdminClient(builder, KC, "onepay", "onepay-backend", "secret", "onepay-mobile");
    }

    private void expectAdminToken() {
        server.expect(requestTo(TOKEN_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().formDataContains(java.util.Map.of(
                        "grant_type", "client_credentials", "client_id", "onepay-backend", "client_secret", "secret")))
                .andRespond(withSuccess("{\"access_token\":\"admin-token\"}", MediaType.APPLICATION_JSON));
    }

    @Test
    void findUserByUsername_returnsFirstExactMatch() {
        expectAdminToken();
        server.expect(requestTo(USERS_URL + "?username=cashier.test&exact=true"))
                .andExpect(header("Authorization", "Bearer admin-token"))
                .andRespond(withSuccess("[{\"id\":\"u1\",\"username\":\"cashier.test\",\"requiredActions\":[\"UPDATE_PASSWORD\"]}]",
                        MediaType.APPLICATION_JSON));

        Optional<KeycloakUser> user = client.findUserByUsername("cashier.test");

        assertThat(user).contains(new KeycloakUser("u1", "cashier.test", List.of("UPDATE_PASSWORD")));
        server.verify();
    }

    @Test
    void findUserByUsername_unknown_returnsEmpty() {
        expectAdminToken();
        server.expect(requestTo(USERS_URL + "?username=ghost&exact=true"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        assertThat(client.findUserByUsername("ghost")).isEmpty();
    }

    @Test
    void updateRequiredActions_putsPartialRepresentation() {
        expectAdminToken();
        server.expect(requestTo(USERS_URL + "/u1"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().json("{\"requiredActions\":[\"VERIFY_EMAIL\"]}", true))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        client.updateRequiredActions("u1", List.of("VERIFY_EMAIL"));
        server.verify();
    }

    @Test
    void checkPassword_validCredentials() {
        server.expect(requestTo(TOKEN_URL))
                .andExpect(content().formDataContains(java.util.Map.of(
                        "grant_type", "password", "client_id", "onepay-mobile", "username", "cashier.test", "password", "Temp123!")))
                .andRespond(withSuccess("{\"access_token\":\"t\"}", MediaType.APPLICATION_JSON));

        assertThat(client.checkPassword("cashier.test", "Temp123!")).isEqualTo(PasswordCheck.VALID);
    }

    @Test
    void checkPassword_invalidCredentials() {
        server.expect(requestTo(TOKEN_URL))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"invalid_grant\",\"error_description\":\"Invalid user credentials\"}"));

        assertThat(client.checkPassword("cashier.test", "bad")).isEqualTo(PasswordCheck.INVALID);
    }

    @Test
    void checkPassword_otherRequiredActionPending() {
        server.expect(requestTo(TOKEN_URL))
                .andRespond(withBadRequest().contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"invalid_grant\",\"error_description\":\"Account is not fully set up\"}"));

        assertThat(client.checkPassword("cashier.test", "Temp123!")).isEqualTo(PasswordCheck.ACCOUNT_NOT_READY);
    }

    @Test
    void checkPassword_errorWithoutBody_isInvalid() {
        server.expect(requestTo(TOKEN_URL)).andRespond(withBadRequest());

        assertThat(client.checkPassword("cashier.test", "x")).isEqualTo(PasswordCheck.INVALID);
    }

    @Test
    void checkPassword_keycloakDown_throwsUnavailable() {
        server.expect(requestTo(TOKEN_URL)).andRespond(withServerError());

        assertThatThrownBy(() -> client.checkPassword("cashier.test", "x"))
                .isInstanceOf(KeycloakUnavailableException.class);
    }

    @Test
    void resetPassword_setsPermanentPassword() {
        expectAdminToken();
        server.expect(requestTo(USERS_URL + "/u1/reset-password"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().json("{\"type\":\"password\",\"value\":\"Nouveau123!\",\"temporary\":false}", true))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        client.resetPassword("u1", "Nouveau123!");
        server.verify();
    }

    @Test
    void resetPassword_rejectedByPolicy_throws422WithDetail() {
        expectAdminToken();
        server.expect(requestTo(USERS_URL + "/u1/reset-password"))
                .andRespond(withBadRequest().contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"invalidPasswordMinLengthMessage\",\"error_description\":\"Invalid password: minimum length 12.\"}"));

        assertThatThrownBy(() -> client.resetPassword("u1", "court"))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessage("Le nouveau mot de passe ne respecte pas la politique de sécurité : Invalid password: minimum length 12.");
    }

    @Test
    void resetPassword_rejectedWithoutDetail_throwsGeneric422() {
        expectAdminToken();
        server.expect(requestTo(USERS_URL + "/u1/reset-password")).andRespond(withBadRequest());

        assertThatThrownBy(() -> client.resetPassword("u1", "court"))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessage("Le nouveau mot de passe ne respecte pas la politique de sécurité.");
    }

    @Test
    void resetPassword_keycloakDown_throwsUnavailable() {
        expectAdminToken();
        server.expect(requestTo(USERS_URL + "/u1/reset-password")).andRespond(withServerError());

        assertThatThrownBy(() -> client.resetPassword("u1", "Nouveau123!"))
                .isInstanceOf(KeycloakUnavailableException.class);
    }

    @Test
    void logoutUser_postsLogout() {
        expectAdminToken();
        server.expect(requestTo(USERS_URL + "/u1/logout"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        client.logoutUser("u1");
        server.verify();
    }

    @Test
    void adminToken_missing_throwsUnavailable() {
        server.expect(requestTo(TOKEN_URL)).andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.findUserByUsername("cashier.test"))
                .isInstanceOf(KeycloakUnavailableException.class)
                .hasMessage("Jeton du compte de service absent");
    }

    @Test
    void adminToken_refused_throwsUnavailable() {
        server.expect(requestTo(TOKEN_URL)).andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        assertThatThrownBy(() -> client.findUserByUsername("cashier.test"))
                .isInstanceOf(KeycloakUnavailableException.class)
                .hasMessage("Service d'authentification indisponible");
    }
}
