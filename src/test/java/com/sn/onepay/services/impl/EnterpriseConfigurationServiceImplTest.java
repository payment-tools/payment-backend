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
        assertThat(entity.isActive()).isTrue();
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void updateEnterpriseConfiguration_throwsWhenNotFound() {
        when(enterpriseConfigurationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enterpriseConfigurationService.updateEnterpriseConfiguration(mock(EnterpriseConfigurationDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateEnterpriseConfiguration_updatesAllFieldsWhenProvided() {
        var dto = mock(EnterpriseConfigurationDTO.class);
        var enterprise = new Enterprise();
        when(dto.enterprise()).thenReturn(enterprise);
        when(dto.maxAmountRestauration()).thenReturn(500.0);
        when(dto.maxAmountMarket()).thenReturn(300.0);
        when(dto.maxAmountGasStation()).thenReturn(200.0);
        when(dto.maxAmountTelephony()).thenReturn(100.0);
        when(dto.enterprisePercentage()).thenReturn(60);
        when(dto.employeePercentage()).thenReturn(40);
        when(dto.active()).thenReturn(true);

        var existing = new EnterpriseConfiguration();
        var saved = new EnterpriseConfiguration();
        var resultDTO = mock(EnterpriseConfigurationDTO.class);

        when(enterpriseConfigurationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(enterpriseConfigurationRepository.saveAndFlush(existing)).thenReturn(saved);
        when(enterpriseConfigurationMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = enterpriseConfigurationService.updateEnterpriseConfiguration(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getEnterprise()).isEqualTo(enterprise);
        assertThat(existing.getMaxAmountRestauration()).isEqualTo(500.0);
        assertThat(existing.getMaxAmountMarket()).isEqualTo(300.0);
        assertThat(existing.getMaxAmountGasStation()).isEqualTo(200.0);
        assertThat(existing.getMaxAmountTelephony()).isEqualTo(100.0);
        assertThat(existing.getEnterprisePercentage()).isEqualTo(60);
        assertThat(existing.getEmployeePercentage()).isEqualTo(40);
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void updateEnterpriseConfiguration_keepsExistingFieldsWhenDtoFieldsNull() {
        /*Mockito's default answer for an unstubbed boxed-type accessor (Double/Boolean) is
          the zero value, not null, so these need to be stubbed explicitly to exercise the
          "field not provided" branch*/
        var dto = mock(EnterpriseConfigurationDTO.class);
        when(dto.maxAmountRestauration()).thenReturn(null);
        when(dto.maxAmountMarket()).thenReturn(null);
        when(dto.maxAmountGasStation()).thenReturn(null);
        when(dto.maxAmountTelephony()).thenReturn(null);
        when(dto.active()).thenReturn(null);

        var existing = new EnterpriseConfiguration();
        existing.setMaxAmountRestauration(999.0);
        existing.setActive(true);
        var saved = new EnterpriseConfiguration();
        var resultDTO = mock(EnterpriseConfigurationDTO.class);

        when(enterpriseConfigurationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(enterpriseConfigurationRepository.saveAndFlush(existing)).thenReturn(saved);
        when(enterpriseConfigurationMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = enterpriseConfigurationService.updateEnterpriseConfiguration(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getMaxAmountRestauration()).isEqualTo(999.0);
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void deleteEnterpriseConfiguration_throwsWhenNotFound() {
        when(enterpriseConfigurationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enterpriseConfigurationService.deleteEnterpriseConfiguration(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteEnterpriseConfiguration_deletesWhenFound() {
        var existing = new EnterpriseConfiguration();
        when(enterpriseConfigurationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(enterpriseConfigurationRepository.saveAndFlush(existing)).thenReturn(existing);

        enterpriseConfigurationService.deleteEnterpriseConfiguration(1L);

        assertThat(existing.isActive()).isFalse();
        verify(enterpriseConfigurationRepository).saveAndFlush(existing);
    }

    @Test
    void getEnterpriseConfigurationsByFilters_withNullPercentages_skipsFilter() {
        var page = new PageImpl<>(List.of(new EnterpriseConfiguration()));
        when(enterpriseConfigurationRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(enterpriseConfigurationMapper.asDTO(any(EnterpriseConfiguration.class))).thenReturn(mock(EnterpriseConfigurationDTO.class));

        Page<EnterpriseConfigurationDTO> result = enterpriseConfigurationService.getEnterpriseConfigurationsByFilters(
                null, null, null, null, null, null, null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getEnterpriseConfigurationsByFilters_withNonZeroPercentages_addsFilter() {
        var page = new PageImpl<>(List.of(new EnterpriseConfiguration()));
        when(enterpriseConfigurationRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(enterpriseConfigurationMapper.asDTO(any(EnterpriseConfiguration.class))).thenReturn(mock(EnterpriseConfigurationDTO.class));

        Page<EnterpriseConfigurationDTO> result = enterpriseConfigurationService.getEnterpriseConfigurationsByFilters(
                1L, 7L, 500.0, 300.0, 200.0, 100.0, 60, 40, true,
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

    @Test
    void getEnterpriseConfigurationById_returnsMappedDTOWhenFound() {
        var entity = new EnterpriseConfiguration();
        var resultDTO = mock(EnterpriseConfigurationDTO.class);

        when(enterpriseConfigurationRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(enterpriseConfigurationMapper.asDTO(entity)).thenReturn(resultDTO);

        var result = enterpriseConfigurationService.getEnterpriseConfigurationById(1L);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void getEnterpriseConfigurationById_throwsWhenNotFound() {
        when(enterpriseConfigurationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enterpriseConfigurationService.getEnterpriseConfigurationById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}