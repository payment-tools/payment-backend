package com.sn.onepay.services.impl;

import com.sn.onepay.dto.EnterpriseProfileCreateDTO;
import com.sn.onepay.dto.EnterpriseProfileDTO;
import com.sn.onepay.dto.EnterpriseProfileUpdateDTO;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.entity.EnterpriseProfile;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.keycloak.AccountFields;
import com.sn.onepay.keycloak.KeycloakAccountService;
import com.sn.onepay.mapper.EnterpriseProfileMapper;
import com.sn.onepay.repository.EnterpriseProfileRepository;
import com.sn.onepay.repository.EnterpriseRepository;
import com.sn.onepay.utils.AccountUniquenessChecker;
import com.sn.onepay.enumeration.PlatformUserType;
import com.sn.onepay.exceptions.ResourceAlreadyExistException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnterpriseProfileServiceImplTest {

    @Mock
    EnterpriseProfileRepository enterpriseProfileRepository;

    @Mock
    EnterpriseRepository enterpriseRepository;

    @Mock
    EnterpriseProfileMapper enterpriseProfileMapper;

    @Mock
    KeycloakAccountService keycloakAccountService;

    @Mock
    AccountUniquenessChecker accountUniquenessChecker;

    @InjectMocks
    EnterpriseProfileServiceImpl enterpriseProfileService;

    @Test
    void createEnterpriseProfile_savesAndReturnsDTO() {
        var createDTO = mock(EnterpriseProfileCreateDTO.class);
        when(createDTO.firstname()).thenReturn("Jean");
        when(createDTO.lastname()).thenReturn("Dupont");
        when(createDTO.username()).thenReturn("jdupont");
        when(createDTO.email()).thenReturn("jean@mail.com");
        when(createDTO.phoneNumber()).thenReturn("770000000");
        when(createDTO.role()).thenReturn(Roles.ENTERPRISE_ADMIN);
        when(createDTO.enterpriseId()).thenReturn(2L);

        var enterprise = new Enterprise();
        var saved = new EnterpriseProfile();
        var resultDTO = mock(EnterpriseProfileDTO.class);

        when(enterpriseRepository.findById(2L)).thenReturn(Optional.of(enterprise));
        when(enterpriseProfileRepository.save(any(EnterpriseProfile.class))).thenReturn(saved);
        when(enterpriseProfileMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = enterpriseProfileService.createEnterpriseProfile(createDTO);

        assertThat(result).isEqualTo(resultDTO);

        ArgumentCaptor<EnterpriseProfile> captor = ArgumentCaptor.forClass(EnterpriseProfile.class);
        verify(enterpriseProfileRepository).save(captor.capture());
        assertThat(captor.getValue().isActive()).isTrue();
        assertThat(captor.getValue().getFirstname()).isEqualTo("Jean");
        assertThat(captor.getValue().getLastname()).isEqualTo("Dupont");
        assertThat(captor.getValue().getUsername()).isEqualTo("jdupont");
        assertThat(captor.getValue().getEmail()).isEqualTo("jean@mail.com");
        assertThat(captor.getValue().getPhoneNumber()).isEqualTo("770000000");
        assertThat(captor.getValue().getRole()).isEqualTo(Roles.ENTERPRISE_ADMIN);
        assertThat(captor.getValue().getEnterprise()).isEqualTo(enterprise);

        verify(keycloakAccountService).createAccount(
                new AccountFields("jdupont", "jean@mail.com", "Jean", "Dupont", Roles.ENTERPRISE_ADMIN, true));
    }

    @Test
    void createEnterpriseProfile_throwsWhenEnterpriseNotFound() {
        var createDTO = mock(EnterpriseProfileCreateDTO.class);
        when(createDTO.enterpriseId()).thenReturn(99L);
        when(enterpriseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enterpriseProfileService.createEnterpriseProfile(createDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateEnterpriseProfile_throwsWhenNotFound() {
        when(enterpriseProfileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enterpriseProfileService.updateEnterpriseProfile(mock(EnterpriseProfileUpdateDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateEnterpriseProfile_updatesAllFieldsWhenProvided() {
        var dto = mock(EnterpriseProfileUpdateDTO.class);
        when(dto.firstname()).thenReturn("Jean");
        when(dto.lastname()).thenReturn("Dupont");
        when(dto.username()).thenReturn("jdupont");
        when(dto.email()).thenReturn("jean@mail.com");
        when(dto.phoneNumber()).thenReturn("770000000");
        when(dto.role()).thenReturn(Roles.ENTERPRISE_ADMIN);
        when(dto.active()).thenReturn(true);

        var existing = new EnterpriseProfile();
        existing.setUsername("jdupont");
        existing.setRole(Roles.ENTERPRISE_ADMIN);
        var saved = new EnterpriseProfile();
        var resultDTO = mock(EnterpriseProfileDTO.class);

        when(enterpriseProfileRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(enterpriseProfileRepository.saveAndFlush(existing)).thenReturn(saved);
        when(enterpriseProfileMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = enterpriseProfileService.updateEnterpriseProfile(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getFirstname()).isEqualTo("Jean");
        assertThat(existing.getLastname()).isEqualTo("Dupont");
        assertThat(existing.getUsername()).isEqualTo("jdupont");
        assertThat(existing.getEmail()).isEqualTo("jean@mail.com");
        assertThat(existing.getPhoneNumber()).isEqualTo("770000000");
        assertThat(existing.getRole()).isEqualTo(Roles.ENTERPRISE_ADMIN);
        assertThat(existing.isActive()).isTrue();

        verify(keycloakAccountService).updateAccount(
                new AccountFields("jdupont", null, null, null, Roles.ENTERPRISE_ADMIN, false),
                new AccountFields("jdupont", "jean@mail.com", "Jean", "Dupont", Roles.ENTERPRISE_ADMIN, true));
    }

    @Test
    void updateEnterpriseProfile_keepsExistingFieldsWhenDtoFieldsNull() {
        /*Mockito's default answer for an unstubbed Boolean accessor is false, not null,
          so it needs to be stubbed explicitly to exercise the "field not provided" branch*/
        var dto = mock(EnterpriseProfileUpdateDTO.class);
        when(dto.active()).thenReturn(null);

        var existing = new EnterpriseProfile();
        existing.setFirstname("Original");
        existing.setActive(true);
        var saved = new EnterpriseProfile();
        var resultDTO = mock(EnterpriseProfileDTO.class);

        when(enterpriseProfileRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(enterpriseProfileRepository.saveAndFlush(existing)).thenReturn(saved);
        when(enterpriseProfileMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = enterpriseProfileService.updateEnterpriseProfile(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getFirstname()).isEqualTo("Original");
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void deleteEnterpriseProfile_throwsWhenNotFound() {
        when(enterpriseProfileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enterpriseProfileService.deleteEnterpriseProfile(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteEnterpriseProfile_setsActiveToFalse() {
        var profile = new EnterpriseProfile();
        profile.setUsername("jdupont");
        when(enterpriseProfileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(enterpriseProfileRepository.saveAndFlush(profile)).thenReturn(profile);

        enterpriseProfileService.deleteEnterpriseProfile(1L);

        assertThat(profile.isActive()).isFalse();
        verify(enterpriseProfileRepository).saveAndFlush(profile);
        verify(keycloakAccountService).disableAccount("jdupont");
    }

    @Test
    void getEnterpriseProfilesByFilters_withNullParams_returnsPage() {
        var page = new PageImpl<>(List.of(new EnterpriseProfile()));
        when(enterpriseProfileRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(enterpriseProfileMapper.asDTO(any(EnterpriseProfile.class))).thenReturn(mock(EnterpriseProfileDTO.class));

        Page<EnterpriseProfileDTO> result = enterpriseProfileService.getEnterpriseProfilesByFilters(
                null, null, null, null, null, null, null, null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getEnterpriseProfilesByFilters_withAllParamsSet_returnsPage() {
        var page = new PageImpl<>(List.of(new EnterpriseProfile()));
        when(enterpriseProfileRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(enterpriseProfileMapper.asDTO(any(EnterpriseProfile.class))).thenReturn(mock(EnterpriseProfileDTO.class));

        Page<EnterpriseProfileDTO> result = enterpriseProfileService.getEnterpriseProfilesByFilters(
                1L, "REF", "John", "Doe", "jdoe", "jdoe@mail.com", "770000000",
                Roles.ENTERPRISE_ADMIN, 2L, true,
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getMyProfile_returnsMappedDTOWhenFound() {
        var profile = new EnterpriseProfile();
        var resultDTO = mock(EnterpriseProfileDTO.class);

        when(enterpriseProfileRepository.findByUsername("jdoe")).thenReturn(Optional.of(profile));
        when(enterpriseProfileMapper.asDTO(profile)).thenReturn(resultDTO);

        var result = enterpriseProfileService.getMyProfile("jdoe");
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void getMyProfile_throwsWhenNotFound() {
        when(enterpriseProfileRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enterpriseProfileService.getMyProfile("unknown"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getEnterpriseProfileById_returnsMappedDTOWhenFound() {
        var entity = new EnterpriseProfile();
        var resultDTO = mock(EnterpriseProfileDTO.class);

        when(enterpriseProfileRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(enterpriseProfileMapper.asDTO(entity)).thenReturn(resultDTO);

        var result = enterpriseProfileService.getEnterpriseProfileById(1L);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void getEnterpriseProfileById_throwsWhenNotFound() {
        when(enterpriseProfileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enterpriseProfileService.getEnterpriseProfileById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createEnterpriseProfile_rejectsMissingRole() {
        var createDTO = mock(EnterpriseProfileCreateDTO.class);
        when(createDTO.role()).thenReturn(null);
        when(createDTO.enterpriseId()).thenReturn(2L);
        when(enterpriseRepository.findById(2L)).thenReturn(Optional.of(new Enterprise()));

        assertThatThrownBy(() -> enterpriseProfileService.createEnterpriseProfile(createDTO))
                .isInstanceOf(ObjectValidationException.class);
        verify(enterpriseProfileRepository, never()).save(any());
        verifyNoInteractions(keycloakAccountService);
    }

    @Test
    void createEnterpriseProfile_rejectsRoleOfAnotherAccountType() {
        var createDTO = mock(EnterpriseProfileCreateDTO.class);
        when(createDTO.role()).thenReturn(Roles.SALES_ADMIN);
        when(createDTO.enterpriseId()).thenReturn(2L);
        when(enterpriseRepository.findById(2L)).thenReturn(Optional.of(new Enterprise()));

        assertThatThrownBy(() -> enterpriseProfileService.createEnterpriseProfile(createDTO))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("SALES_ADMIN");
        verify(enterpriseProfileRepository, never()).save(any());
        verifyNoInteractions(keycloakAccountService);
    }

    @Test
    void updateEnterpriseProfile_rejectsUsernameChange() {
        var dto = mock(EnterpriseProfileUpdateDTO.class);
        when(dto.username()).thenReturn("autre.username");
        var existing = new EnterpriseProfile();
        existing.setUsername("jdupont");
        existing.setRole(Roles.ENTERPRISE_ADMIN);
        when(enterpriseProfileRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> enterpriseProfileService.updateEnterpriseProfile(dto, 1L))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("username");
        verify(enterpriseProfileRepository, never()).saveAndFlush(any());
        verifyNoInteractions(keycloakAccountService);
    }

    @Test
    void updateEnterpriseProfile_rejectsRoleChange() {
        var dto = mock(EnterpriseProfileUpdateDTO.class);
        when(dto.role()).thenReturn(Roles.SALES_ADMIN);
        var existing = new EnterpriseProfile();
        existing.setUsername("jdupont");
        existing.setRole(Roles.ENTERPRISE_ADMIN);
        when(enterpriseProfileRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> enterpriseProfileService.updateEnterpriseProfile(dto, 1L))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("rôle");
        verify(enterpriseProfileRepository, never()).saveAndFlush(any());
        verifyNoInteractions(keycloakAccountService);
    }

    @Test
    void createEnterpriseProfile_rejectsWhenAccountFieldAlreadyUsed() {
        var dto = new EnterpriseProfileCreateDTO("Jean", "Dupont", "jdupont", "jean@mail.com", "771234567", Roles.ENTERPRISE_ADMIN, 1L);
        doThrow(new ResourceAlreadyExistException("Ce numéro de téléphone est déjà utilisé par un autre compte : 771234567"))
                .when(accountUniquenessChecker).checkAvailable(PlatformUserType.ENTERPRISE_PROFILE, null, "jdupont", "jean@mail.com", "771234567");

        assertThatThrownBy(() -> enterpriseProfileService.createEnterpriseProfile(dto)).isInstanceOf(ResourceAlreadyExistException.class);
        verify(enterpriseProfileRepository, never()).save(any());
    }

    @Test
    void updateEnterpriseProfile_checksUniquenessExcludingItself() {
        var dto = new EnterpriseProfileUpdateDTO(null, null, null, "new@mail.com", "+221 70 000 00 00", null, null);
        doThrow(new ResourceAlreadyExistException("Cet e-mail est déjà utilisé par un autre compte : new@mail.com"))
                .when(accountUniquenessChecker).checkAvailable(PlatformUserType.ENTERPRISE_PROFILE, 7L, null, "new@mail.com", "+221 70 000 00 00");

        assertThatThrownBy(() -> enterpriseProfileService.updateEnterpriseProfile(dto, 7L)).isInstanceOf(ResourceAlreadyExistException.class);
        verify(enterpriseProfileRepository, never()).saveAndFlush(any());
    }
}
