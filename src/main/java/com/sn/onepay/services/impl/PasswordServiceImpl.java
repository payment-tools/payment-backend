package com.sn.onepay.services.impl;

import com.sn.onepay.dto.FirstPasswordChangeDTO;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.keycloak.KeycloakAdminClient;
import com.sn.onepay.keycloak.KeycloakAdminClient.KeycloakUser;
import com.sn.onepay.keycloak.KeycloakAdminClient.PasswordCheck;
import com.sn.onepay.services.PasswordService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sn.onepay.keycloak.KeycloakAdminClient.UPDATE_PASSWORD;

/*Keycloak refuse le grant password tant que l'action UPDATE_PASSWORD est en attente, et n'expose aucun endpoint
  de vérification de mot de passe : l'action est retirée le temps de vérifier le mot de passe temporaire, puis
  restaurée si la vérification ou la définition du nouveau mot de passe échoue*/
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PasswordServiceImpl implements PasswordService {

    /*Même message pour un compte inconnu, sans mot de passe temporaire ou un mauvais mot de passe : pas d'énumération*/
    static final String INVALID_CREDENTIALS = "Identifiant ou mot de passe temporaire incorrect.";
    static final String SAME_PASSWORD = "Le nouveau mot de passe doit être différent du mot de passe temporaire.";
    static final String ACCOUNT_NOT_READY = "Votre compte doit être finalisé par votre gestionnaire.";

    KeycloakAdminClient keycloakAdminClient;

    @Override
    public void changeTemporaryPassword(FirstPasswordChangeDTO firstPasswordChangeDTO) {
        String username = firstPasswordChangeDTO.username().trim();
        log.info("First password change requested for {}", username);

        if (firstPasswordChangeDTO.newPassword().equals(firstPasswordChangeDTO.temporaryPassword())) {
            throw new ObjectValidationException(SAME_PASSWORD);
        }
        KeycloakUser user = keycloakAdminClient.findUserByUsername(username)
                .filter(found -> found.requiredActions() != null && found.requiredActions().contains(UPDATE_PASSWORD))
                .orElseThrow(() -> new ObjectValidationException(INVALID_CREDENTIALS));

        List<String> remainingActions = user.requiredActions().stream()
                .filter(action -> !UPDATE_PASSWORD.equals(action))
                .toList();
        keycloakAdminClient.updateRequiredActions(user.id(), remainingActions);
        try {
            PasswordCheck check = keycloakAdminClient.checkPassword(username, firstPasswordChangeDTO.temporaryPassword());
            if (check != PasswordCheck.VALID) {
                throw new ObjectValidationException(check == PasswordCheck.INVALID ? INVALID_CREDENTIALS : ACCOUNT_NOT_READY);
            }
            keycloakAdminClient.resetPassword(user.id(), firstPasswordChangeDTO.newPassword());
        } catch (RuntimeException exception) {
            keycloakAdminClient.updateRequiredActions(user.id(), user.requiredActions());
            throw exception;
        }

        // Ferme la session ouverte par la vérification : l'app se reconnecte avec le nouveau mot de passe
        keycloakAdminClient.logoutUser(user.id());
        log.info("First password changed for {}", username);
        log.trace("Keycloak user id {}", user.id());
    }
}
