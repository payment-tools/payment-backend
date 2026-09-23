package com.sn.onepay.services.impl;

import com.sn.onepay.dto.SalesConfigurationsDTO;
import com.sn.onepay.entity.Sales;
import com.sn.onepay.entity.SalesConfigurations;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.SalesConfigurationsMapper;
import com.sn.onepay.repository.SalesConfigurationsRepository;
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
class SalesConfigurationsServiceImplTest {

    @Mock
    SalesConfigurationsRepository salesConfigurationsRepository;

    @Mock
    SalesConfigurationsMapper salesConfigurationsMapper;

    @InjectMocks
    SalesConfigurationsServiceImpl salesConfigurationsService;

    @Test
    void createSalesConfigurations_savesAndReturnsDTO() {
        var dto = mock(SalesConfigurationsDTO.class);
        var entity = new SalesConfigurations();
        var saved = new SalesConfigurations();
        var resultDTO = mock(SalesConfigurationsDTO.class);

        when(salesConfigurationsMapper.asEntity(dto)).thenReturn(entity);
        when(salesConfigurationsRepository.save(entity)).thenReturn(saved);
        when(salesConfigurationsMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = salesConfigurationsService.createSalesConfigurations(dto);
        assertThat(entity.isActive()).isTrue();
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void updateSalesConfigurations_throwsWhenNotFound() {
        when(salesConfigurationsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salesConfigurationsService.updateSalesConfigurations(mock(SalesConfigurationsDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateSalesConfigurations_updatesAllFieldsWhenProvided() {
        var dto = mock(SalesConfigurationsDTO.class);
        var sales = new Sales();
        when(dto.sales()).thenReturn(sales);
        when(dto.minAmount()).thenReturn(10.0);
        when(dto.maxAmount()).thenReturn(1000.0);
        when(dto.active()).thenReturn(true);

        var existing = new SalesConfigurations();
        var saved = new SalesConfigurations();
        var resultDTO = mock(SalesConfigurationsDTO.class);

        when(salesConfigurationsRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(salesConfigurationsRepository.saveAndFlush(existing)).thenReturn(saved);
        when(salesConfigurationsMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = salesConfigurationsService.updateSalesConfigurations(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getSales()).isEqualTo(sales);
        assertThat(existing.getMinAmount()).isEqualTo(10.0);
        assertThat(existing.getMaxAmount()).isEqualTo(1000.0);
        assertThat(existing.isActive()).isTrue();
        verify(salesConfigurationsRepository).saveAndFlush(existing);
    }

    @Test
    void updateSalesConfigurations_keepsExistingFieldsWhenDtoFieldsNull() {
        /*Mockito's default answer for an unstubbed boxed-type accessor (Double/Boolean) is
          the zero value, not null, so these need to be stubbed explicitly to exercise the
          "field not provided" branch*/
        var dto = mock(SalesConfigurationsDTO.class);
        when(dto.minAmount()).thenReturn(null);
        when(dto.maxAmount()).thenReturn(null);
        when(dto.active()).thenReturn(null);

        var existing = new SalesConfigurations();
        existing.setMinAmount(50.0);
        existing.setActive(true);
        var saved = new SalesConfigurations();
        var resultDTO = mock(SalesConfigurationsDTO.class);

        when(salesConfigurationsRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(salesConfigurationsRepository.saveAndFlush(existing)).thenReturn(saved);
        when(salesConfigurationsMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = salesConfigurationsService.updateSalesConfigurations(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getMinAmount()).isEqualTo(50.0);
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void deleteSalesConfigurations_throwsWhenNotFound() {
        when(salesConfigurationsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salesConfigurationsService.deleteSalesConfigurations(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteSalesConfigurations_deletesWhenFound() {
        var existing = new SalesConfigurations();
        when(salesConfigurationsRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(salesConfigurationsRepository.saveAndFlush(existing)).thenReturn(existing);

        salesConfigurationsService.deleteSalesConfigurations(1L);

        assertThat(existing.isActive()).isFalse();
        verify(salesConfigurationsRepository).saveAndFlush(existing);
    }

    @Test
    void getSalesConfigurationsByFilters_withNullParams_returnsPage() {
        var page = new PageImpl<>(List.of(new SalesConfigurations()));
        when(salesConfigurationsRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(salesConfigurationsMapper.asDTO(any(SalesConfigurations.class))).thenReturn(mock(SalesConfigurationsDTO.class));

        Page<SalesConfigurationsDTO> result = salesConfigurationsService.getSalesConfigurationsByFilters(
                null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getSalesConfigurationsByFilters_withAllParamsSet_returnsPage() {
        var page = new PageImpl<>(List.of(new SalesConfigurations()));
        when(salesConfigurationsRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(salesConfigurationsMapper.asDTO(any(SalesConfigurations.class))).thenReturn(mock(SalesConfigurationsDTO.class));

        Page<SalesConfigurationsDTO> result = salesConfigurationsService.getSalesConfigurationsByFilters(
                1L, 2L, true, LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getSalesConfigurationsBySalesId_returnsDTO() {
        var resultDTO = mock(SalesConfigurationsDTO.class);
        when(salesConfigurationsRepository.getSalesConfigurationsBySalesId(1L)).thenReturn(resultDTO);

        var result = salesConfigurationsService.getSalesConfigurationsBySalesId(1L);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void getSalesConfigurationsById_returnsMappedDTOWhenFound() {
        var entity = new SalesConfigurations();
        var resultDTO = mock(SalesConfigurationsDTO.class);

        when(salesConfigurationsRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(salesConfigurationsMapper.asDTO(entity)).thenReturn(resultDTO);

        var result = salesConfigurationsService.getSalesConfigurationsById(1L);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void getSalesConfigurationsById_throwsWhenNotFound() {
        when(salesConfigurationsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salesConfigurationsService.getSalesConfigurationsById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}