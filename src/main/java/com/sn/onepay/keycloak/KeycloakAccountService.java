package com.sn.onepay.keycloak;

import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceAlreadyExistException;
import com.sn.onepay.keycloak.KeycloakAdminClient.KeycloakUser;
import com.sn.onepay.keycloak.KeycloakAdminClient.NewUser;
import com.sn.onepay.mail.AccountInvitation;
import com.sn.onepay.mail.AccountMailer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/*Comptes de connexion Keycloak des utilisateurs créés dans le back-office (Client, Cashier,
  EnterpriseProfile, SalesProfile) : création avec rôle realm + mot de passe temporaire envoyé par e-mail,
  synchronisation du profil et de l'activation. Le lien entre fiche métier et compte est le username.*/
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeycloakAccountService {

    static final String PASSWORD_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
    static final int PASSWORD_LENGTH = 12;

    final KeycloakAdminClient keycloakAdminClient;
    final AccountMailer accountMailer;
    final SecureRandom random = new SecureRandom();

    /*À appeler dans la transaction de création de la fiche : l'e-mail part après le commit,
      et le compte Keycloak est supprimé si la transaction est annulée*/
    public void createAccount(AccountFields account) {
        String username = account.username();
        Roles role = account.role();
        Optional<KeycloakUser> existing = keycloakAdminClient.findUserByUsername(username);
        if (existing.isPresent()) {
            // Compte déjà présent (ex. comptes de test du realm) : rattaché seulement s'il a déjà ce rôle,
            // pour qu'une création de fiche ne puisse jamais élever les droits d'un compte existant
            if (keycloakAdminClient.getRealmRoles(existing.get().id()).contains(role.name())) {
                log.info("Linked existing Keycloak account: {}", username);
                return;
            }
            throw new ResourceAlreadyExistException("Un compte de connexion existe déjà pour ce username avec un autre rôle : " + username);
        }

        String temporaryPassword = generateTemporaryPassword();
        String userId = keycloakAdminClient.createUser(new NewUser(username, account.email(), account.firstname(), account.lastname(), temporaryPassword));
        try {
            keycloakAdminClient.assignRealmRole(userId, role.name());
        } catch (RuntimeException exception) {
            deleteQuietly(userId, username);
            throw exception;
        }
        log.info("Created Keycloak account: {} with role {}", username, role);
        log.trace("Created Keycloak account with id: {}", userId);

        AccountInvitation invitation = new AccountInvitation(account.email(), account.firstname(), account.lastname(), username, temporaryPassword);
        afterTransaction(() -> sendQuietly(invitation), () -> deleteQuietly(userId, username));
    }

    /*Reporte dans Keycloak les champs modifiés. Fiche sans compte Keycloak (créée avant le provisioning) : ignorée*/
    public void updateAccount(AccountFields before, AccountFields after) {
        String email = changed(before.email(), after.email());
        String firstname = changed(before.firstname(), after.firstname());
        String lastname = changed(before.lastname(), after.lastname());
        Boolean enabled = before.active() == after.active() ? null : after.active();
        if (email == null && firstname == null && lastname == null && enabled == null) {
            return;
        }
        keycloakAdminClient.findUserByUsername(before.username()).ifPresentOrElse(
                user -> keycloakAdminClient.updateUser(user.id(), email, firstname, lastname, enabled),
                () -> log.warn("No Keycloak account to update for user: {}", before.username()));
    }

    public void disableAccount(String username) {
        keycloakAdminClient.findUserByUsername(username).ifPresentOrElse(
                user -> keycloakAdminClient.updateUser(user.id(), null, null, null, false),
                () -> log.warn("No Keycloak account to disable for user: {}", username));
    }

    /*Rôle de la fiche à créer : celui demandé (ou le rôle par défaut) s'il fait partie des rôles autorisés
      pour ce type de compte — empêche par ex. de créer un profil entreprise avec un rôle commerce*/
    public static Roles requireRole(Roles requested, Roles defaultRole, Set<Roles> allowed) {
        Roles role = requested == null ? defaultRole : requested;
        if (role == null) {
            throw new ObjectValidationException("Le rôle est obligatoire (attendu : " + allowed + ")");
        }
        if (!allowed.contains(role)) {
            throw new ObjectValidationException("Rôle non autorisé pour ce type de compte : " + role + " (attendu : " + allowed + ")");
        }
        return role;
    }

    /*Le username relie la fiche au compte Keycloak et le rôle en fixe les droits : ni l'un ni l'autre
      ne sont modifiables après création*/
    public static void checkUpdatable(AccountFields before, AccountFields after) {
        if (!Objects.equals(before.username(), after.username())) {
            throw new ObjectValidationException("Le username d'un compte ne peut pas être modifié");
        }
        if (before.role() != after.role()) {
            throw new ObjectValidationException("Le rôle d'un compte ne peut pas être modifié");
        }
    }

    private static String changed(String before, String after) {
        return Objects.equals(before, after) ? null : after;
    }

    String generateTemporaryPassword() {
        String password;
        do {
            StringBuilder builder = new StringBuilder(PASSWORD_LENGTH);
            for (int i = 0; i < PASSWORD_LENGTH; i++) {
                builder.append(PASSWORD_ALPHABET.charAt(random.nextInt(PASSWORD_ALPHABET.length())));
            }
            password = builder.toString();
        } while (!password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*") || !password.matches(".*[0-9].*"));
        return password;
    }

    private void afterTransaction(Runnable onCommit, Runnable onRollback) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            onCommit.run();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                onCommit.run();
            }

            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    onRollback.run();
                }
            }
        });
    }

    /*Le compte existe déjà : un échec d'envoi ne doit pas faire échouer la création (mot de passe
      à réinitialiser depuis la console Keycloak)*/
    private void sendQuietly(AccountInvitation invitation) {
        try {
            accountMailer.sendInvitation(invitation);
        } catch (RuntimeException exception) {
            log.error("Failed to send credentials e-mail to user {}: {}", invitation.username(), exception.getMessage());
        }
    }

    private void deleteQuietly(String userId, String username) {
        try {
            keycloakAdminClient.deleteUser(userId);
            log.info("Rolled back Keycloak account: {}", username);
        } catch (RuntimeException exception) {
            log.error("Failed to roll back Keycloak account {}: {}", username, exception.getMessage());
        }
    }
}
