package com.sn.onepay.utils;

import com.querydsl.core.types.Predicate;
import com.sn.onepay.enumeration.PlatformUserType;
import com.sn.onepay.exceptions.ResourceAlreadyExistException;
import com.sn.onepay.repository.CashierRepository;
import com.sn.onepay.repository.ClientRepository;
import com.sn.onepay.repository.EnterpriseProfileRepository;
import com.sn.onepay.repository.SalesProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountUniquenessCheckerTest {

    @Mock
    ClientRepository clientRepository;

    @Mock
    CashierRepository cashierRepository;

    @Mock
    EnterpriseProfileRepository enterpriseProfileRepository;

    @Mock
    SalesProfileRepository salesProfileRepository;

    @InjectMocks
    AccountUniquenessChecker checker;

    @Test
    void checkAvailable_passesWhenNoAccountUsesTheValues() {
        assertThatCode(() -> checker.checkAvailable(PlatformUserType.CLIENT, null, "jdupont", "jean@mail.com", "771234567"))
                .doesNotThrowAnyException();

        verify(clientRepository, times(3)).exists(any(Predicate.class));
        verify(salesProfileRepository, times(3)).exists(any(Predicate.class));
    }

    @Test
    void checkAvailable_rejectsUsernameUsedByAnotherAccountType() {
        when(cashierRepository.exists(any(Predicate.class))).thenReturn(true);

        assertThatThrownBy(() -> checker.checkAvailable(PlatformUserType.CLIENT, null, "jdupont", null, null))
                .isInstanceOf(ResourceAlreadyExistException.class)
                .hasMessage("Ce username est déjà utilisé par un autre compte : jdupont");
    }

    @Test
    void checkAvailable_rejectsEmailUsedByAnotherAccount() {
        when(enterpriseProfileRepository.exists(any(Predicate.class))).thenReturn(true);

        assertThatThrownBy(() -> checker.checkAvailable(PlatformUserType.CASHIER, null, null, "jean@mail.com", null))
                .isInstanceOf(ResourceAlreadyExistException.class)
                .hasMessage("Cet e-mail est déjà utilisé par un autre compte : jean@mail.com");
    }

    @Test
    void checkAvailable_rejectsPhoneNumberUsedByAnotherAccount_afterNormalization() {
        ArgumentCaptor<Predicate> predicate = ArgumentCaptor.forClass(Predicate.class);
        when(salesProfileRepository.exists(predicate.capture())).thenReturn(true);

        assertThatThrownBy(() -> checker.checkAvailable(PlatformUserType.CLIENT, null, null, null, "+221 77 123 45 67"))
                .isInstanceOf(ResourceAlreadyExistException.class)
                .hasMessage("Ce numéro de téléphone est déjà utilisé par un autre compte : 771234567");
        assertThat(predicate.getValue().toString()).isEqualTo("salesProfile.phoneNumber = 771234567");
    }

    @Test
    void checkAvailable_comparesUsernameAndEmailIgnoringCase() {
        ArgumentCaptor<Predicate> predicate = ArgumentCaptor.forClass(Predicate.class);
        when(clientRepository.exists(predicate.capture())).thenReturn(false);

        checker.checkAvailable(PlatformUserType.CASHIER, null, "JDupont", "Jean@Mail.com", null);

        assertThat(predicate.getAllValues()).extracting(Object::toString)
                .containsExactly("eqIc(client.username,JDupont)", "eqIc(client.email,Jean@Mail.com)");
    }

    @Test
    void checkAvailable_excludesTheUpdatedAccountOnlyInItsOwnTable() {
        ArgumentCaptor<Predicate> enterpriseProfilePredicate = ArgumentCaptor.forClass(Predicate.class);
        ArgumentCaptor<Predicate> clientPredicate = ArgumentCaptor.forClass(Predicate.class);
        when(enterpriseProfileRepository.exists(enterpriseProfilePredicate.capture())).thenReturn(false);
        when(clientRepository.exists(clientPredicate.capture())).thenReturn(false);

        checker.checkAvailable(PlatformUserType.ENTERPRISE_PROFILE, 7L, null, null, "771234567");

        assertThat(enterpriseProfilePredicate.getValue().toString()).isEqualTo("enterpriseProfile.phoneNumber = 771234567 && enterpriseProfile.id != 7");
        assertThat(clientPredicate.getValue().toString()).isEqualTo("client.phoneNumber = 771234567");
    }

    @Test
    void checkAvailable_skipsNullFields() {
        checker.checkAvailable(PlatformUserType.SALES_PROFILE, 3L, null, null, null);

        verify(clientRepository, never()).exists(any(Predicate.class));
        verify(cashierRepository, never()).exists(any(Predicate.class));
        verify(enterpriseProfileRepository, never()).exists(any(Predicate.class));
        verify(salesProfileRepository, never()).exists(any(Predicate.class));
    }
}
