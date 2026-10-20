package com.sn.onepay.keycloak;

import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceAlreadyExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/*Client minimal de l'API REST Keycloak, authentifié par le compte de service onepay-backend
  (client confidentiel, rôles realm-management view-users + manage-users ; pas de view-realm :
  les rôles sont lus via les role-mappings de l'utilisateur)*/
@Slf4j
@Component
public class KeycloakAdminClient {

    public static final String UPDATE_PASSWORD = "UPDATE_PASSWORD";

    /*error_description renvoyée par le grant password quand une action requise est en attente*/
    static final String ACCOUNT_NOT_FULLY_SET_UP = "Account is not fully set up";

    public record KeycloakUser(String id, String username, List<String> requiredActions) {
    }

    public enum PasswordCheck {VALID, INVALID, ACCOUNT_NOT_READY}

    public record NewUser(String username, String email, String firstName, String lastName, String temporaryPassword) {
    }

    record RoleRepresentation(String id, String name) {
    }

    private final RestClient restClient;
    private final String realm;
    private final String adminClientId;
    private final String adminClientSecret;
    private final String loginClientId;

    public KeycloakAdminClient(RestClient.Builder restClientBuilder,
                               @Value("${onepay.keycloak.server-url}") String serverUrl,
                               @Value("${onepay.keycloak.realm}") String realm,
                               @Value("${onepay.keycloak.admin-client-id}") String adminClientId,
                               @Value("${onepay.keycloak.admin-client-secret}") String adminClientSecret,
                               @Value("${onepay.keycloak.login-client-id}") String loginClientId) {
        this.restClient = restClientBuilder.baseUrl(serverUrl).build();
        this.realm = realm;
        this.adminClientId = adminClientId;
        this.adminClientSecret = adminClientSecret;
        this.loginClientId = loginClientId;
    }

    public Optional<KeycloakUser> findUserByUsername(String username) {
        KeycloakUser[] users = call(() -> restClient.get()
                .uri("/admin/realms/{realm}/users?username={username}&exact=true", realm, username)
                .header(HttpHeaders.AUTHORIZATION, bearer())
                .retrieve()
                .body(KeycloakUser[].class));
        return users == null || users.length == 0 ? Optional.empty() : Optional.of(users[0]);
    }

    /*Crée un compte actif avec un mot de passe temporaire (=> action requise UPDATE_PASSWORD) ; renvoie son id*/
    public String createUser(NewUser newUser) {
        Map<String, Object> representation = Map.of(
                "username", newUser.username(),
                "email", newUser.email(),
                "firstName", newUser.firstName() == null ? "" : newUser.firstName(),
                "lastName", newUser.lastName() == null ? "" : newUser.lastName(),
                "enabled", true,
                "emailVerified", true,
                "credentials", List.of(Map.of("type", "password", "value", newUser.temporaryPassword(), "temporary", true)));
        ResponseEntity<Void> response;
        try {
            response = restClient.post()
                    .uri("/admin/realms/{realm}/users", realm)
                    .header(HttpHeaders.AUTHORIZATION, bearer())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(representation)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.Conflict exception) {
            Map<?, ?> body = exception.getResponseBodyAs(Map.class);
            String detail = body == null ? null : (String) body.get("errorMessage");
            throw new ResourceAlreadyExistException(detail != null && detail.contains("email")
                    ? "Un compte de connexion existe déjà avec cet email : " + newUser.email()
                    : "Un compte de connexion existe déjà avec ce username : " + newUser.username());
        } catch (RestClientException exception) {
            throw unavailable(exception);
        }
        URI location = response.getHeaders().getLocation();
        if (location == null) {
            throw new KeycloakUnavailableException("Identifiant du compte créé absent de la réponse Keycloak", null);
        }
        String path = location.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    /*Noms des rôles realm directement assignés à l'utilisateur*/
    public List<String> getRealmRoles(String userId) {
        RoleRepresentation[] roles = call(() -> restClient.get()
                .uri("/admin/realms/{realm}/users/{id}/role-mappings/realm", realm, userId)
                .header(HttpHeaders.AUTHORIZATION, bearer())
                .retrieve()
                .body(RoleRepresentation[].class));
        return roles == null ? List.of() : Arrays.stream(roles).map(RoleRepresentation::name).toList();
    }

    /*Assigne un rôle realm ; sans effet s'il est déjà assigné. La représentation du rôle est lue dans
      la liste des rôles assignables à l'utilisateur (GET /roles/{name} exigerait view-realm)*/
    public void assignRealmRole(String userId, String roleName) {
        RoleRepresentation[] available = call(() -> restClient.get()
                .uri("/admin/realms/{realm}/users/{id}/role-mappings/realm/available", realm, userId)
                .header(HttpHeaders.AUTHORIZATION, bearer())
                .retrieve()
                .body(RoleRepresentation[].class));
        Optional<RoleRepresentation> role = available == null ? Optional.empty()
                : Arrays.stream(available).filter(candidate -> roleName.equals(candidate.name())).findFirst();
        if (role.isEmpty()) {
            if (getRealmRoles(userId).contains(roleName)) {
                return;
            }
            throw new KeycloakUnavailableException("Rôle realm introuvable dans Keycloak : " + roleName, null);
        }
        call(() -> restClient.post()
                .uri("/admin/realms/{realm}/users/{id}/role-mappings/realm", realm, userId)
                .header(HttpHeaders.AUTHORIZATION, bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .body(List.of(role.get()))
                .retrieve()
                .toBodilessEntity());
    }

    /*Mise à jour partielle des informations de profil et de l'état du compte (champs null ignorés)*/
    public void updateUser(String userId, String email, String firstName, String lastName, Boolean enabled) {
        Map<String, Object> representation = new HashMap<>();
        if (email != null) representation.put("email", email);
        if (firstName != null) representation.put("firstName", firstName);
        if (lastName != null) representation.put("lastName", lastName);
        if (enabled != null) representation.put("enabled", enabled);
        if (representation.isEmpty()) {
            return;
        }
        try {
            restClient.put()
                    .uri("/admin/realms/{realm}/users/{id}", realm, userId)
                    .header(HttpHeaders.AUTHORIZATION, bearer())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(representation)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.Conflict exception) {
            throw new ResourceAlreadyExistException("Un compte de connexion existe déjà avec cet email : " + email);
        } catch (RestClientException exception) {
            throw unavailable(exception);
        }
    }

    public void deleteUser(String userId) {
        call(() -> restClient.delete()
                .uri("/admin/realms/{realm}/users/{id}", realm, userId)
                .header(HttpHeaders.AUTHORIZATION, bearer())
                .retrieve()
                .toBodilessEntity());
    }

    /*Mise à jour partielle : Keycloak ne modifie que les champs présents dans la représentation*/
    public void updateRequiredActions(String userId, List<String> requiredActions) {
        call(() -> restClient.put()
                .uri("/admin/realms/{realm}/users/{id}", realm, userId)
                .header(HttpHeaders.AUTHORIZATION, bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("requiredActions", requiredActions))
                .retrieve()
                .toBodilessEntity());
    }

    /*Vérifie un mot de passe par le grant password du client de connexion mobile*/
    public PasswordCheck checkPassword(String username, String password) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", loginClientId);
        form.add("username", username);
        form.add("password", password);
        try {
            restClient.post()
                    .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .toBodilessEntity();
            return PasswordCheck.VALID;
        } catch (HttpClientErrorException exception) {
            Map<?, ?> body = exception.getResponseBodyAs(Map.class);
            if (body != null && ACCOUNT_NOT_FULLY_SET_UP.equals(body.get("error_description"))) {
                return PasswordCheck.ACCOUNT_NOT_READY;
            }
            return PasswordCheck.INVALID;
        } catch (RestClientException exception) {
            throw unavailable(exception);
        }
    }

    /*Mot de passe définitif ; un refus de la politique de mots de passe du realm est remonté en 422*/
    public void resetPassword(String userId, String newPassword) {
        try {
            restClient.put()
                    .uri("/admin/realms/{realm}/users/{id}/reset-password", realm, userId)
                    .header(HttpHeaders.AUTHORIZATION, bearer())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("type", "password", "value", newPassword, "temporary", false))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.BadRequest exception) {
            Map<?, ?> body = exception.getResponseBodyAs(Map.class);
            Object detail = body == null ? null : body.get("error_description");
            throw new ObjectValidationException(detail == null
                    ? "Le nouveau mot de passe ne respecte pas la politique de sécurité."
                    : "Le nouveau mot de passe ne respecte pas la politique de sécurité : " + detail);
        } catch (RestClientException exception) {
            throw unavailable(exception);
        }
    }

    /*Ferme les sessions ouvertes (dont celle créée par checkPassword)*/
    public void logoutUser(String userId) {
        call(() -> restClient.post()
                .uri("/admin/realms/{realm}/users/{id}/logout", realm, userId)
                .header(HttpHeaders.AUTHORIZATION, bearer())
                .retrieve()
                .toBodilessEntity());
    }

    private String bearer() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", adminClientId);
        form.add("client_secret", adminClientSecret);
        Map<?, ?> token = call(() -> restClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(Map.class));
        if (token == null || token.get("access_token") == null) {
            throw new KeycloakUnavailableException("Jeton du compte de service absent", null);
        }
        return "Bearer " + token.get("access_token");
    }

    private <T> T call(java.util.function.Supplier<T> request) {
        try {
            return request.get();
        } catch (RestClientException exception) {
            throw unavailable(exception);
        }
    }

    private KeycloakUnavailableException unavailable(RestClientException exception) {
        log.error("Keycloak admin API call failed: {}", exception.getMessage());
        return new KeycloakUnavailableException("Service d'authentification indisponible", exception);
    }
}
