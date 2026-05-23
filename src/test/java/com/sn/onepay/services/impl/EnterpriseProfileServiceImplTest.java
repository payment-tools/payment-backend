package com.sn.onepay.services.impl;

import com.sn.onepay.dto.EnterpriseProfileDTO;
import com.sn.onepay.entity.EnterpriseProfile;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.EnterpriseProfileMapper;
import com.sn.onepay.repository.EnterpriseProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    EnterpriseProfileMapper enterpriseProfileMapper;

    @InjectMocks
    EnterpriseProfileServiceImpl enterpriseProfileService;

    @Test
    void createEnterpriseProfile_savesAndReturnsDTO() {
        var dto = mock(EnterpriseProfileDTO.class);
        var entity = new EnterpriseProfile();
        var saved = new EnterpriseProfile();
        var resultDTO = mock(EnterpriseProfileDTO.class);

        when(enterpriseProfileMapper.asEntity(dto)).thenReturn(entity);
        when(enterpriseProfileRepository.save(any(EnterpriseProfile.class))).thenReturn(saved);
        when(enterpriseProfileMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = enterpriseProfileService.createEnterpriseProfile(dto);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(entity.isActive()).isTrue();
    }

    @Test
    void updateEnterpriseProfile_throwsWhenNotFound() {
        when(enterpriseProfileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enterpriseProfileService.updateEnterpriseProfile(mock(EnterpriseProfileDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateEnterpriseProfile_savesWhenFound() {
        var dto = mock(EnterpriseProfileDTO.class);
        var existing = new EnterpriseProfile();
        var updated = new EnterpriseProfile();
        var resultDTO = mock(EnterpriseProfileDTO.class);

        when(enterpriseProfileRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(enterpriseProfileMapper.asEntity(dto)).thenReturn(updated);
        when(enterpriseProfileRepository.save(updated)).thenReturn(updated);
        when(enterpriseProfileMapper.asDTO(updated)).thenReturn(resultDTO);

        var result = enterpriseProfileService.updateEnterpriseProfile(dto, 1L);
        assertThat(result).isEqualTo(resultDTO);
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
        when(enterpriseProfileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(enterpriseProfileRepository.saveAndFlush(profile)).thenReturn(profile);

        enterpriseProfileService.deleteEnterpriseProfile(1L);

        assertThat(profile.isActive()).isFalse();
        verify(enterpriseProfileRepository).saveAndFlush(profile);
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
}