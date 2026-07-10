package com.sn.onepay.services.impl;

import com.sn.onepay.dto.BillsDTO;
import com.sn.onepay.entity.Bills;
import com.sn.onepay.enumeration.BillStatus;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.BillsMapper;
import com.sn.onepay.repository.BillsRepository;
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
class BillsServiceImplTest {

    @Mock
    BillsRepository billsRepository;

    @Mock
    BillsMapper billsMapper;

    @InjectMocks
    BillsServiceImpl billsService;

    @Test
    void createBills_happyPath() {
        var dto = mock(BillsDTO.class);
        var entity = new Bills();
        var saved = new Bills();
        var resultDTO = mock(BillsDTO.class);

        when(billsMapper.asEntity(dto)).thenReturn(entity);
        when(billsRepository.save(any(Bills.class))).thenReturn(saved);
        when(billsMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = billsService.createBills(dto);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(entity.isActive()).isTrue();
        assertThat(entity.getRef()).isNotNull();
    }

    @Test
    void updateBills_throwsWhenNotFound() {
        when(billsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> billsService.updateBills(mock(BillsDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateBills_modifiesFieldsAndSaves() {
        var startDate = LocalDateTime.of(2026, 7, 1, 0, 0);
        var endDate = LocalDateTime.of(2026, 7, 31, 23, 59);

        var dto = mock(BillsDTO.class);
        when(dto.startDate()).thenReturn(startDate);
        when(dto.endDate()).thenReturn(endDate);
        when(dto.totalAmount()).thenReturn(5000.0);
        when(dto.billStatus()).thenReturn(BillStatus.PAYED);
        when(dto.period()).thenReturn("2026-07");
        when(dto.active()).thenReturn(false);

        var existing = new Bills();
        existing.setActive(true);
        var updated = new Bills();
        var resultDTO = mock(BillsDTO.class);

        when(billsRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(billsRepository.saveAndFlush(existing)).thenReturn(updated);
        when(billsMapper.asDTO(updated)).thenReturn(resultDTO);

        var result = billsService.updateBills(dto, 1L);

        assertThat(existing.getStartDate()).isEqualTo(startDate);
        assertThat(existing.getEndDate()).isEqualTo(endDate);
        assertThat(existing.getTotalAmount()).isEqualTo(5000.0);
        assertThat(existing.getBillStatus()).isEqualTo(BillStatus.PAYED);
        assertThat(existing.getPeriod()).isEqualTo("2026-07");
        assertThat(existing.isActive()).isFalse();
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void updateBills_withNullFields_doesNotChangeFields() {
        var startDate = LocalDateTime.of(2026, 7, 1, 0, 0);

        var dto = mock(BillsDTO.class);
        when(dto.startDate()).thenReturn(null);
        when(dto.endDate()).thenReturn(null);
        when(dto.totalAmount()).thenReturn(null);
        when(dto.billStatus()).thenReturn(null);
        when(dto.period()).thenReturn(null);
        when(dto.active()).thenReturn(null);

        var existing = new Bills();
        existing.setStartDate(startDate);
        existing.setTotalAmount(3000.0);
        existing.setBillStatus(BillStatus.UNPAYED);
        existing.setPeriod("2026-06");
        existing.setActive(true);

        var updated = new Bills();
        var resultDTO = mock(BillsDTO.class);

        when(billsRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(billsRepository.saveAndFlush(existing)).thenReturn(updated);
        when(billsMapper.asDTO(updated)).thenReturn(resultDTO);

        billsService.updateBills(dto, 1L);

        assertThat(existing.getStartDate()).isEqualTo(startDate);
        assertThat(existing.getTotalAmount()).isEqualTo(3000.0);
        assertThat(existing.getBillStatus()).isEqualTo(BillStatus.UNPAYED);
        assertThat(existing.getPeriod()).isEqualTo("2026-06");
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void deleteBills_throwsWhenNotFound() {
        when(billsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> billsService.deleteBills(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteBills_setsActiveToFalse() {
        var bills = new Bills();
        bills.setActive(true);
        when(billsRepository.findById(1L)).thenReturn(Optional.of(bills));
        when(billsRepository.saveAndFlush(bills)).thenReturn(bills);

        billsService.deleteBills(1L);

        assertThat(bills.isActive()).isFalse();
        verify(billsRepository).saveAndFlush(bills);
    }

    @Test
    void getBillsByFilters_withNullParams_returnsPage() {
        var page = new PageImpl<>(List.of(new Bills()));
        when(billsRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(billsMapper.asDTO(any(Bills.class))).thenReturn(mock(BillsDTO.class));

        Page<BillsDTO> result = billsService.getBillsByFilters(
                null, null, null, null, null, null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getBillsByFilters_withAllParamsSet_returnsPage() {
        var page = new PageImpl<>(List.of(new Bills()));
        when(billsRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(billsMapper.asDTO(any(Bills.class))).thenReturn(mock(BillsDTO.class));

        Page<BillsDTO> result = billsService.getBillsByFilters(
                1L, "REF", 2L, BillStatus.UNPAYED, "2026-07", true,
                LocalDateTime.now().minusMonths(1), LocalDateTime.now(),
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getBillsByFilters_withEmptyRefAndPeriod_returnsPage() {
        var page = new PageImpl<>(List.of(new Bills()));
        when(billsRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(billsMapper.asDTO(any(Bills.class))).thenReturn(mock(BillsDTO.class));

        Page<BillsDTO> result = billsService.getBillsByFilters(
                null, "", null, null, "", null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }
}
