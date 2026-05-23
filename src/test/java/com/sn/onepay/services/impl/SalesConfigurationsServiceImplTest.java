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
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void updateSalesConfigurations_throwsWhenNotFound() {
        when(salesConfigurationsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salesConfigurationsService.updateSalesConfigurations(mock(SalesConfigurationsDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateSalesConfigurations_returnsWhenFound() {
        // Note: the existing service has a bug (does not save), but tests what exists
        var dto = mock(SalesConfigurationsDTO.class);
        var entity = new SalesConfigurations();
        var resultDTO = mock(SalesConfigurationsDTO.class);

        when(salesConfigurationsRepository.findById(1L)).thenReturn(Optional.of(new SalesConfigurations()));
        when(salesConfigurationsMapper.asEntity(dto)).thenReturn(entity);
        when(salesConfigurationsMapper.asDTO(entity)).thenReturn(resultDTO);

        var result = salesConfigurationsService.updateSalesConfigurations(dto, 1L);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void deleteSalesConfigurations_throwsWhenNotFound() {
        when(salesConfigurationsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salesConfigurationsService.deleteSalesConfigurations(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteSalesConfigurations_deletesWhenFound() {
        when(salesConfigurationsRepository.findById(1L)).thenReturn(Optional.of(new SalesConfigurations()));
        doNothing().when(salesConfigurationsRepository).deleteById(1L);

        salesConfigurationsService.deleteSalesConfigurations(1L);

        verify(salesConfigurationsRepository).deleteById(1L);
    }

    @Test
    void getSalesConfigurationsByFilters_withNullParams_returnsPage() {
        var page = new PageImpl<>(List.of(new SalesConfigurations()));
        when(salesConfigurationsRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(salesConfigurationsMapper.asDTO(any(SalesConfigurations.class))).thenReturn(mock(SalesConfigurationsDTO.class));

        Page<SalesConfigurationsDTO> result = salesConfigurationsService.getSalesConfigurationsByFilters(
                null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getSalesConfigurationsByFilters_withAllParamsSet_returnsPage() {
        var page = new PageImpl<>(List.of(new SalesConfigurations()));
        when(salesConfigurationsRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(salesConfigurationsMapper.asDTO(any(SalesConfigurations.class))).thenReturn(mock(SalesConfigurationsDTO.class));

        Page<SalesConfigurationsDTO> result = salesConfigurationsService.getSalesConfigurationsByFilters(
                1L, mock(Sales.class), LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getSalesConfigurationsBySalesId_returnsDTO() {
        var resultDTO = mock(SalesConfigurationsDTO.class);
        when(salesConfigurationsRepository.getSalesConfigurationsBySalesId(1L)).thenReturn(resultDTO);

        var result = salesConfigurationsService.getSalesConfigurationsBySalesId(1L);
        assertThat(result).isEqualTo(resultDTO);
    }
}