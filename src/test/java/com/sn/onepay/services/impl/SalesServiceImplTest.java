package com.sn.onepay.services.impl;

import com.sn.onepay.dto.SalesDTO;
import com.sn.onepay.entity.Sales;
import com.sn.onepay.enumeration.Modules;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.SalesMapper;
import com.sn.onepay.repository.SalesRepository;
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
class SalesServiceImplTest {

    @Mock
    SalesRepository salesRepository;

    @Mock
    SalesMapper salesMapper;

    @InjectMocks
    SalesServiceImpl salesService;

    @Test
    void createSales_savesAndReturnsDTO() {
        var dto = mock(SalesDTO.class);
        var entity = new Sales();
        var saved = new Sales();
        var resultDTO = mock(SalesDTO.class);

        when(salesMapper.asEntity(dto)).thenReturn(entity);
        when(salesRepository.save(entity)).thenReturn(saved);
        when(salesMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = salesService.createSales(dto);
        assertThat(entity.isActive()).isTrue();
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void updateSales_throwsWhenNotFound() {
        when(salesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salesService.updateSales(mock(SalesDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateSales_updatesAllFieldsWhenProvided() {
        var dto = mock(SalesDTO.class);
        when(dto.ref()).thenReturn("REF1");
        when(dto.name()).thenReturn("RestaurantX");
        when(dto.address()).thenReturn("Dakar");
        when(dto.type()).thenReturn(Modules.RESTAURATION);
        when(dto.active()).thenReturn(true);

        var existing = new Sales();
        var saved = new Sales();
        var resultDTO = mock(SalesDTO.class);

        when(salesRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(salesRepository.saveAndFlush(existing)).thenReturn(saved);
        when(salesMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = salesService.updateSales(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getRef()).isEqualTo("REF1");
        assertThat(existing.getName()).isEqualTo("RestaurantX");
        assertThat(existing.getAddress()).isEqualTo("Dakar");
        assertThat(existing.getType()).isEqualTo(Modules.RESTAURATION);
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void updateSales_keepsExistingFieldsWhenDtoFieldsNull() {
        /*Mockito's default answer for an unstubbed Boolean accessor is false, not null,
          so it needs to be stubbed explicitly to exercise the "field not provided" branch*/
        var dto = mock(SalesDTO.class);
        when(dto.active()).thenReturn(null);

        var existing = new Sales();
        existing.setName("Original");
        existing.setActive(true);
        var saved = new Sales();
        var resultDTO = mock(SalesDTO.class);

        when(salesRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(salesRepository.saveAndFlush(existing)).thenReturn(saved);
        when(salesMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = salesService.updateSales(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getName()).isEqualTo("Original");
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void deleteSales_throwsWhenNotFound() {
        when(salesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salesService.deleteSales(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteSales_deletesWhenFound() {
        var existing = new Sales();
        when(salesRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(salesRepository.saveAndFlush(existing)).thenReturn(existing);

        salesService.deleteSales(1L);

        assertThat(existing.isActive()).isFalse();
        verify(salesRepository).saveAndFlush(existing);
    }

    @Test
    void getSalesByFilters_withNullId_skipsIdFilter() {
        var page = new PageImpl<>(List.of(new Sales()));
        when(salesRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(salesMapper.asDTO(any(Sales.class))).thenReturn(mock(SalesDTO.class));

        Page<SalesDTO> result = salesService.getSalesByFilters(
                null, null, null, null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getSalesByFilters_withNonNullId_addsIdFilter() {
        var page = new PageImpl<>(List.of(new Sales()));
        when(salesRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(salesMapper.asDTO(any(Sales.class))).thenReturn(mock(SalesDTO.class));

        Page<SalesDTO> result = salesService.getSalesByFilters(
                1L, "REF", "Sales Name", Modules.RESTAURATION, "Dakar", true,
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }
}