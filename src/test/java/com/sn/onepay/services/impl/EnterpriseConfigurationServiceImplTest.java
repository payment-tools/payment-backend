package com.sn.onepay.services.impl;

import com.sn.onepay.dto.EnterpriseConfigurationDTO;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.entity.EnterpriseConfiguration;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.EnterpriseConfigurationMapper;
import com.sn.onepay.repository.EnterpriseConfigurationRepository;
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
class EnterpriseConfigurationServiceImplTest {

    @Mock
    EnterpriseConfigurationRepository enterpriseConfigurationRepository;

    @Mock
    EnterpriseConfigurationMapper enterpriseConfigurationMapper;

    @InjectMocks
    EnterpriseConfigurationServiceImpl enterpriseConfigurationService;

    @Test
    void createEnterpriseConfiguration_savesAndReturnsDTO() {
        var dto = mock(EnterpriseConfigurationDTO.class);
        var entity = new EnterpriseConfiguration();
        var saved = new EnterpriseConfiguration();
        var resultDTO = mock(EnterpriseConfigurationDTO.class);

        when(enterpriseConfigurationMapper.asEntity(dto)).thenReturn(entity);
        when(enterpriseConfigurationRepository.save(entity)).thenReturn(saved);
        when(enterpriseConfigurationMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = enterpriseConfigurationService.createEnterpriseConfiguration(dto);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void updateEnterpriseConfiguration_throwsWhenNotFound() {
        when(enterpriseConfigurationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enterpriseConfigurationService.updateEnterpriseConfiguration(mock(EnterpriseConfigurationDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateEnterpriseConfiguration_savesWhenFound() {
        var dto = mock(EnterpriseConfigurationDTO.class);
        var existing = new EnterpriseConfiguration();
        var updated = new EnterpriseConfiguration();
        var resultDTO = mock(EnterpriseConfigurationDTO.class);

        when(enterpriseConfigurationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(enterpriseConfigurationMapper.asEntity(dto)).thenReturn(updated);
        when(enterpriseConfigurationRepository.saveAndFlush(updated)).thenReturn(updated);
        when(enterpriseConfigurationMapper.asDTO(updated)).thenReturn(resultDTO);

        var result = enterpriseConfigurationService.updateEnterpriseConfiguration(dto, 1L);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void deleteEnterpriseConfiguration_throwsWhenNotFound() {
        when(enterpriseConfigurationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enterpriseConfigurationService.deleteEnterpriseConfiguration(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteEnterpriseConfiguration_deletesWhenFound() {
        when(enterpriseConfigurationRepository.findById(1L)).thenReturn(Optional.of(new EnterpriseConfiguration()));
        doNothing().when(enterpriseConfigurationRepository).deleteById(1L);

        enterpriseConfigurationService.deleteEnterpriseConfiguration(1L);

        verify(enterpriseConfigurationRepository).deleteById(1L);
    }

    @Test
    void getEnterpriseConfigurationsByFilters_withNullPercentages_skipsFilter() {
        var page = new PageImpl<>(List.of(new EnterpriseConfiguration()));
        when(enterpriseConfigurationRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(enterpriseConfigurationMapper.asDTO(any(EnterpriseConfiguration.class))).thenReturn(mock(EnterpriseConfigurationDTO.class));

        Page<EnterpriseConfigurationDTO> result = enterpriseConfigurationService.getEnterpriseConfigurationsByFilters(
                null, null, null, null, null, null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getEnterpriseConfigurationsByFilters_withNonZeroPercentages_addsFilter() {
        var page = new PageImpl<>(List.of(new EnterpriseConfiguration()));
        when(enterpriseConfigurationRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(enterpriseConfigurationMapper.asDTO(any(EnterpriseConfiguration.class))).thenReturn(mock(EnterpriseConfigurationDTO.class));

        Page<EnterpriseConfigurationDTO> result = enterpriseConfigurationService.getEnterpriseConfigurationsByFilters(
                1L, mock(Enterprise.class), 500.0, 300.0, 200.0, 100.0, 60, 40,
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getEnterpriseConfigurationByEnterpriseId_returnsMappedDTO() {
        var entity = new EnterpriseConfiguration();
        var resultDTO = mock(EnterpriseConfigurationDTO.class);

        when(enterpriseConfigurationRepository.getEnterpriseConfigurationByEnterpriseId(1L)).thenReturn(entity);
        when(enterpriseConfigurationMapper.asDTO(entity)).thenReturn(resultDTO);

        var result = enterpriseConfigurationService.getEnterpriseConfigurationByEnterpriseId(1L);
        assertThat(result).isEqualTo(resultDTO);
    }
}