package com.sn.onepay.services.impl;

import com.sn.onepay.dto.CashierDTO;
import com.sn.onepay.entity.Cashier;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.CashierMapper;
import com.sn.onepay.repository.CashierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashierServiceImplTest {

    @Mock
    CashierRepository cashierRepository;

    @Mock
    CashierMapper cashierMapper;

    @InjectMocks
    CashierServiceImpl cashierService;

    @Test
    void createCashier_savesAndReturnsDTO() {
        var dto = mock(CashierDTO.class);
        var entity = new Cashier();
        var saved = new Cashier();
        var resultDTO = mock(CashierDTO.class);

        when(cashierMapper.asEntity(dto)).thenReturn(entity);
        when(cashierRepository.save(any(Cashier.class))).thenReturn(saved);
        when(cashierMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = cashierService.createCashier(dto);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(entity.isActive()).isTrue();
    }

    @Test
    void updateCashier_throwsWhenNotFound() {
        when(cashierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cashierService.updateCashier(mock(CashierDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateCashier_savesWhenFound() {
        var dto = mock(CashierDTO.class);
        var existing = new Cashier();
        var updated = new Cashier();
        var resultDTO = mock(CashierDTO.class);

        when(cashierRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(cashierMapper.asEntity(dto)).thenReturn(updated);
        when(cashierRepository.saveAndFlush(updated)).thenReturn(updated);
        when(cashierMapper.asDTO(updated)).thenReturn(resultDTO);

        var result = cashierService.updateCashier(dto, 1L);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void deleteCashier_throwsWhenNotFound() {
        when(cashierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cashierService.deleteCashier(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteCashier_setsActiveToFalse() {
        var cashier = new Cashier();
        when(cashierRepository.findById(1L)).thenReturn(Optional.of(cashier));
        when(cashierRepository.saveAndFlush(cashier)).thenReturn(cashier);

        cashierService.deleteCashier(1L);

        assertThat(cashier.isActive()).isFalse();
        verify(cashierRepository).saveAndFlush(cashier);
    }

    @Test
    void getCashiersByFilter_returnsNull() {
        var result = cashierService.getCashiersByFilter(mock(CashierDTO.class), Pageable.unpaged());
        assertThat(result).isNull();
    }
}