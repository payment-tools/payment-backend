package com.sn.onepay.services.impl;

import com.sn.onepay.dto.FirstPasswordChangeDTO;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.keycloak.KeycloakAdminClient;
import com.sn.onepay.keycloak.KeycloakAdminClient.KeycloakUser;
import com.sn.onepay.keycloak.KeycloakAdminClient.PasswordCheck;
import com.sn.onepay.keycloak.KeycloakUnavailableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordServiceImplTest {

    static final FirstPasswordChangeDTO REQUEST = new FirstPasswordChangeDTO(" cashier.test ", "Temp123!", "Nouveau123!");
    static final KeycloakUser USER = new KeycloakUser("u1", "cashier.test", List.of("UPDATE_PASSWORD", "VERIFY_EMAIL"));

    @Mock
    KeycloakAdminClient keycloakAdminClient;

    @InjectMocks
    PasswordServiceImpl passwordService;

    @Test
    void changeTemporaryPassword_success_removesActionVerifiesResetsAndLogsOut() {
        when(keycloakAdminClient.findUserByUsername("cashier.test")).thenReturn(Optional.of(USER));
        when(keycloakAdminClient.checkPassword("cashier.test", "Temp123!")).thenReturn(PasswordCheck.VALID);

        passwordService.changeTemporaryPassword(REQUEST);

        InOrder order = inOrder(keycloakAdminClient);
        order.verify(keycloakAdminClient).updateRequiredActions("u1", List.of("VERIFY_EMAIL"));
        order.verify(keycloakAdminClient).checkPassword("cashier.test", "Temp123!");
        order.verify(keycloakAdminClient).resetPassword("u1", "Nouveau123!");
        order.verify(keycloakAdminClient).logoutUser("u1");
        verify(keycloakAdminClient, never()).updateRequiredActions("u1", USER.requiredActions());
    }

    @Test
    void sameAsTemporary_rejectedBeforeAnyKeycloakCall() {
        assertThatThrownBy(() -> passwordService.changeTemporaryPassword(
                new FirstPasswordChangeDTO("cashier.test", "Temp123!", "Temp123!")))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessage(PasswordServiceImpl.SAME_PASSWORD);
        verify(keycloakAdminClient, never()).findUserByUsername(anyString());
    }

    @Test
    void unknownUser_genericMessage() {
        when(keycloakAdminClient.findUserByUsername("cashier.test")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passwordService.changeTemporaryPassword(REQUEST))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessage(PasswordServiceImpl.INVALID_CREDENTIALS);
    }

    @Test
    void userWithoutPendingPasswordChange_genericMessage_noChange() {
        when(keycloakAdminClient.findUserByUsername("cashier.test"))
                .thenReturn(Optional.of(new KeycloakUser("u1", "cashier.test", List.of())));

        assertThatThrownBy(() -> passwordService.changeTemporaryPassword(REQUEST))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessage(PasswordServiceImpl.INVALID_CREDENTIALS);
        verify(keycloakAdminClient, never()).updateRequiredActions(anyString(), any());
    }

    @Test
    void userWithNullRequiredActions_genericMessage() {
        when(keycloakAdminClient.findUserByUsername("cashier.test"))
                .thenReturn(Optional.of(new KeycloakUser("u1", "cashier.test", null)));

        assertThatThrownBy(() -> passwordService.changeTemporaryPassword(REQUEST))
                .hasMessage(PasswordServiceImpl.INVALID_CREDENTIALS);
    }

    @Test
    void wrongTemporaryPassword_restoresActions() {
        when(keycloakAdminClient.findUserByUsername("cashier.test")).thenReturn(Optional.of(USER));
        when(keycloakAdminClient.checkPassword("cashier.test", "Temp123!")).thenReturn(PasswordCheck.INVALID);

        assertThatThrownBy(() -> passwordService.changeTemporaryPassword(REQUEST))
                .hasMessage(PasswordServiceImpl.INVALID_CREDENTIALS);
        verify(keycloakAdminClient).updateRequiredActions("u1", USER.requiredActions());
        verify(keycloakAdminClient, never()).resetPassword(anyString(), anyString());
        verify(keycloakAdminClient, never()).logoutUser(anyString());
    }

    @Test
    void otherPendingAction_restoresActions_specificMessage() {
        when(keycloakAdminClient.findUserByUsername("cashier.test")).thenReturn(Optional.of(USER));
        when(keycloakAdminClient.checkPassword("cashier.test", "Temp123!")).thenReturn(PasswordCheck.ACCOUNT_NOT_READY);

        assertThatThrownBy(() -> passwordService.changeTemporaryPassword(REQUEST))
                .hasMessage(PasswordServiceImpl.ACCOUNT_NOT_READY);
        verify(keycloakAdminClient).updateRequiredActions("u1", USER.requiredActions());
    }

    @Test
    void newPasswordRejectedByPolicy_restoresActions_propagates() {
        when(keycloakAdminClient.findUserByUsername("cashier.test")).thenReturn(Optional.of(USER));
        when(keycloakAdminClient.checkPassword("cashier.test", "Temp123!")).thenReturn(PasswordCheck.VALID);
        doThrow(new ObjectValidationException("politique")).when(keycloakAdminClient).resetPassword("u1", "Nouveau123!");

        assertThatThrownBy(() -> passwordService.changeTemporaryPassword(REQUEST)).hasMessage("politique");
        verify(keycloakAdminClient).updateRequiredActions("u1", USER.requiredActions());
    }

    @Test
    void keycloakDownDuringCheck_restoresActions_propagates() {
        when(keycloakAdminClient.findUserByUsername("cashier.test")).thenReturn(Optional.of(USER));
        when(keycloakAdminClient.checkPassword("cashier.test", "Temp123!"))
                .thenThrow(new KeycloakUnavailableException("down", null));

        assertThatThrownBy(() -> passwordService.changeTemporaryPassword(REQUEST))
                .isInstanceOf(KeycloakUnavailableException.class);
        verify(keycloakAdminClient).updateRequiredActions("u1", USER.requiredActions());
    }
}
