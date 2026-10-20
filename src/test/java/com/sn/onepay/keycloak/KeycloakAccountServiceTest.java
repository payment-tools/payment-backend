package com.sn.onepay.keycloak;

import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceAlreadyExistException;
import com.sn.onepay.keycloak.KeycloakAdminClient.KeycloakUser;
import com.sn.onepay.keycloak.KeycloakAdminClient.NewUser;
import com.sn.onepay.mail.AccountInvitation;
import com.sn.onepay.mail.AccountMailer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakAccountServiceTest {

    static final AccountFields ACCOUNT = new AccountFields("jdupont", "jean@mail.com", "Jean", "Dupont", Roles.CLIENT, true);

    @Mock
    KeycloakAdminClient keycloakAdminClient;

    @Mock
    AccountMailer accountMailer;

    @InjectMocks
    KeycloakAccountService keycloakAccountService;

    @AfterEach
    void clearTransactionSynchronization() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void createAccount_createsUserWithRoleAndSendsTemporaryPassword() {
        when(keycloakAdminClient.findUserByUsername("jdupont")).thenReturn(Optional.empty());
        when(keycloakAdminClient.createUser(any())).thenReturn("u1");

        keycloakAccountService.createAccount(ACCOUNT);

        ArgumentCaptor<NewUser> newUser = ArgumentCaptor.forClass(NewUser.class);
        verify(keycloakAdminClient).createUser(newUser.capture());
        assertThat(newUser.getValue().username()).isEqualTo("jdupont");
        assertThat(newUser.getValue().email()).isEqualTo("jean@mail.com");
        assertThat(newUser.getValue().firstName()).isEqualTo("Jean");
        assertThat(newUser.getValue().lastName()).isEqualTo("Dupont");
        verify(keycloakAdminClient).assignRealmRole("u1", "CLIENT");

        ArgumentCaptor<AccountInvitation> invitation = ArgumentCaptor.forClass(AccountInvitation.class);
        verify(accountMailer).sendInvitation(invitation.capture());
        assertThat(invitation.getValue()).isEqualTo(new AccountInvitation(
                "jean@mail.com", "Jean", "Dupont", "jdupont", newUser.getValue().temporaryPassword()));
    }

    @Test
    void createAccount_linksExistingAccountThatAlreadyHasTheRole() {
        when(keycloakAdminClient.findUserByUsername("jdupont")).thenReturn(Optional.of(new KeycloakUser("u1", "jdupont", List.of())));
        when(keycloakAdminClient.getRealmRoles("u1")).thenReturn(List.of("CLIENT", "default-roles-onepay"));

        keycloakAccountService.createAccount(ACCOUNT);

        verify(keycloakAdminClient, never()).createUser(any());
        verify(keycloakAdminClient, never()).assignRealmRole(anyString(), anyString());
        verifyNoInteractions(accountMailer);
    }

    @Test
    void createAccount_rejectsExistingAccountWithAnotherRole() {
        when(keycloakAdminClient.findUserByUsername("jdupont")).thenReturn(Optional.of(new KeycloakUser("u1", "jdupont", List.of())));
        when(keycloakAdminClient.getRealmRoles("u1")).thenReturn(List.of("SUPER_ADMIN"));

        assertThatThrownBy(() -> keycloakAccountService.createAccount(ACCOUNT))
                .isInstanceOf(ResourceAlreadyExistException.class)
                .hasMessageContaining("jdupont");
        verify(keycloakAdminClient, never()).createUser(any());
        verify(keycloakAdminClient, never()).assignRealmRole(anyString(), anyString());
        verifyNoInteractions(accountMailer);
    }

    @Test
    void createAccount_deletesUserWhenRoleAssignmentFails() {
        when(keycloakAdminClient.findUserByUsername("jdupont")).thenReturn(Optional.empty());
        when(keycloakAdminClient.createUser(any())).thenReturn("u1");
        var failure = new KeycloakUnavailableException("down", null);
        doThrow(failure).when(keycloakAdminClient).assignRealmRole("u1", "CLIENT");

        assertThatThrownBy(() -> keycloakAccountService.createAccount(ACCOUNT)).isSameAs(failure);
        verify(keycloakAdminClient).deleteUser("u1");
        verifyNoInteractions(accountMailer);
    }

    @Test
    void createAccount_rethrowsRoleFailureEvenWhenCleanupFails() {
        when(keycloakAdminClient.findUserByUsername("jdupont")).thenReturn(Optional.empty());
        when(keycloakAdminClient.createUser(any())).thenReturn("u1");
        var failure = new KeycloakUnavailableException("down", null);
        doThrow(failure).when(keycloakAdminClient).assignRealmRole("u1", "CLIENT");
        doThrow(new KeycloakUnavailableException("still down", null)).when(keycloakAdminClient).deleteUser("u1");

        assertThatThrownBy(() -> keycloakAccountService.createAccount(ACCOUNT)).isSameAs(failure);
    }

    @Test
    void createAccount_insideTransaction_sendsMailOnlyAfterCommit() {
        TransactionSynchronizationManager.initSynchronization();
        when(keycloakAdminClient.findUserByUsername("jdupont")).thenReturn(Optional.empty());
        when(keycloakAdminClient.createUser(any())).thenReturn("u1");

        keycloakAccountService.createAccount(ACCOUNT);

        verifyNoInteractions(accountMailer);
        List<TransactionSynchronization> synchronizations = TransactionSynchronizationManager.getSynchronizations();
        assertThat(synchronizations).hasSize(1);
        synchronizations.get(0).afterCommit();
        synchronizations.get(0).afterCompletion(TransactionSynchronization.STATUS_COMMITTED);

        verify(accountMailer).sendInvitation(any());
        verify(keycloakAdminClient, never()).deleteUser(anyString());
    }

    @Test
    void createAccount_insideTransaction_deletesUserOnRollback() {
        TransactionSynchronizationManager.initSynchronization();
        when(keycloakAdminClient.findUserByUsername("jdupont")).thenReturn(Optional.empty());
        when(keycloakAdminClient.createUser(any())).thenReturn("u1");

        keycloakAccountService.createAccount(ACCOUNT);
        TransactionSynchronizationManager.getSynchronizations().get(0)
                .afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);

        verify(keycloakAdminClient).deleteUser("u1");
        verifyNoInteractions(accountMailer);
    }

    @Test
    void createAccount_doesNotFailWhenMailCannotBeSent() {
        when(keycloakAdminClient.findUserByUsername("jdupont")).thenReturn(Optional.empty());
        when(keycloakAdminClient.createUser(any())).thenReturn("u1");
        doThrow(new IllegalStateException("smtp down")).when(accountMailer).sendInvitation(any());

        assertThatCode(() -> keycloakAccountService.createAccount(ACCOUNT)).doesNotThrowAnyException();
        verify(keycloakAdminClient, never()).deleteUser(anyString());
    }

    @Test
    void updateAccount_sendsOnlyChangedFields() {
        when(keycloakAdminClient.findUserByUsername("jdupont")).thenReturn(Optional.of(new KeycloakUser("u1", "jdupont", List.of())));
        var after = new AccountFields("jdupont", "new@mail.com", "Jean", "Martin", Roles.CLIENT, false);

        keycloakAccountService.updateAccount(ACCOUNT, after);

        verify(keycloakAdminClient).updateUser("u1", "new@mail.com", null, "Martin", false);
    }

    @Test
    void updateAccount_sendsChangedFirstnameAndReactivation() {
        when(keycloakAdminClient.findUserByUsername("jdupont")).thenReturn(Optional.of(new KeycloakUser("u1", "jdupont", List.of())));
        var before = new AccountFields("jdupont", "jean@mail.com", "Jean", "Dupont", Roles.CLIENT, false);
        var after = new AccountFields("jdupont", "jean@mail.com", "Jeanne", "Dupont", Roles.CLIENT, true);

        keycloakAccountService.updateAccount(before, after);

        verify(keycloakAdminClient).updateUser("u1", null, "Jeanne", null, true);
    }

    @Test
    void updateAccount_doesNothingWhenNothingChanged() {
        keycloakAccountService.updateAccount(ACCOUNT, ACCOUNT);

        verifyNoInteractions(keycloakAdminClient);
    }

    @Test
    void updateAccount_ignoresUserWithoutKeycloakAccount() {
        when(keycloakAdminClient.findUserByUsername("jdupont")).thenReturn(Optional.empty());

        keycloakAccountService.updateAccount(ACCOUNT, new AccountFields("jdupont", "new@mail.com", "Jean", "Dupont", Roles.CLIENT, true));

        verify(keycloakAdminClient, never()).updateUser(any(), any(), any(), any(), any());
    }

    @Test
    void disableAccount_disablesExistingAccount() {
        when(keycloakAdminClient.findUserByUsername("jdupont")).thenReturn(Optional.of(new KeycloakUser("u1", "jdupont", List.of())));

        keycloakAccountService.disableAccount("jdupont");

        verify(keycloakAdminClient).updateUser("u1", null, null, null, false);
    }

    @Test
    void disableAccount_ignoresUserWithoutKeycloakAccount() {
        when(keycloakAdminClient.findUserByUsername("jdupont")).thenReturn(Optional.empty());

        keycloakAccountService.disableAccount("jdupont");

        verify(keycloakAdminClient, never()).updateUser(any(), any(), any(), any(), any());
    }

    @Test
    void requireRole_returnsRequestedAllowedRole() {
        assertThat(KeycloakAccountService.requireRole(Roles.ENTERPRISE_FINANCE, null,
                Set.of(Roles.ENTERPRISE_ADMIN, Roles.ENTERPRISE_FINANCE))).isEqualTo(Roles.ENTERPRISE_FINANCE);
    }

    @Test
    void requireRole_fallsBackToDefaultRole() {
        assertThat(KeycloakAccountService.requireRole(null, Roles.CLIENT, Set.of(Roles.CLIENT))).isEqualTo(Roles.CLIENT);
    }

    @Test
    void requireRole_rejectsMissingRoleWithoutDefault() {
        assertThatThrownBy(() -> KeycloakAccountService.requireRole(null, null, Set.of(Roles.SALES_ADMIN)))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("obligatoire");
    }

    @Test
    void requireRole_rejectsRoleOutsideAllowedSet() {
        assertThatThrownBy(() -> KeycloakAccountService.requireRole(Roles.SALES_ADMIN, null, Set.of(Roles.ENTERPRISE_ADMIN)))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("SALES_ADMIN");
    }

    @Test
    void checkUpdatable_acceptsProfileChanges() {
        assertThatCode(() -> KeycloakAccountService.checkUpdatable(ACCOUNT,
                new AccountFields("jdupont", "new@mail.com", "Jeanne", "Martin", Roles.CLIENT, false)))
                .doesNotThrowAnyException();
    }

    @Test
    void checkUpdatable_rejectsUsernameChange() {
        assertThatThrownBy(() -> KeycloakAccountService.checkUpdatable(ACCOUNT,
                new AccountFields("other", "jean@mail.com", "Jean", "Dupont", Roles.CLIENT, true)))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("username");
    }

    @Test
    void checkUpdatable_rejectsRoleChange() {
        assertThatThrownBy(() -> KeycloakAccountService.checkUpdatable(ACCOUNT,
                new AccountFields("jdupont", "jean@mail.com", "Jean", "Dupont", Roles.CASHIER, true)))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("rôle");
    }

    @Test
    void generateTemporaryPassword_mixesUppercaseLowercaseAndDigits() {
        for (int i = 0; i < 200; i++) {
            String password = keycloakAccountService.generateTemporaryPassword();
            assertThat(password).hasSize(KeycloakAccountService.PASSWORD_LENGTH)
                    .containsPattern("[A-Z]").containsPattern("[a-z]").containsPattern("[0-9]")
                    .matches("[" + KeycloakAccountService.PASSWORD_ALPHABET + "]+");
        }
    }
}
