package com.sn.onepay.services.impl;

import com.sn.onepay.dto.CashierDTO;
import com.sn.onepay.dto.SalesDTO;
import com.sn.onepay.entity.Cashier;
import com.sn.onepay.entity.Sales;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.CashierMapper;
import com.sn.onepay.repository.CashierRepository;
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
        assertThat(entity.getRef()).isNotBlank();
    }

    @Test
    void updateCashier_throwsWhenNotFound() {
        when(cashierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cashierService.updateCashier(mock(CashierDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateCashier_updatesAllFieldsWhenProvided() {
        var dto = mock(CashierDTO.class);
        when(dto.ref()).thenReturn("REF1");
        when(dto.firstname()).thenReturn("Jean");
        when(dto.lastname()).thenReturn("Dupont");
        when(dto.username()).thenReturn("jdupont");
        when(dto.email()).thenReturn("jean@mail.com");
        when(dto.phoneNumber()).thenReturn("770000000");
        when(dto.role()).thenReturn(Roles.CASHIER);
        when(dto.sales()).thenReturn(mock(SalesDTO.class));
        when(dto.active()).thenReturn(true);

        var existing = new Cashier();
        var mappedSales = new Sales();
        var mapped = new Cashier();
        mapped.setSales(mappedSales);
        var saved = new Cashier();
        var resultDTO = mock(CashierDTO.class);

        when(cashierRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(cashierMapper.asEntity(dto)).thenReturn(mapped);
        when(cashierRepository.saveAndFlush(existing)).thenReturn(saved);
        when(cashierMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = cashierService.updateCashier(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getRef()).isEqualTo("REF1");
        assertThat(existing.getFirstname()).isEqualTo("Jean");
        assertThat(existing.getLastname()).isEqualTo("Dupont");
        assertThat(existing.getUsername()).isEqualTo("jdupont");
        assertThat(existing.getEmail()).isEqualTo("jean@mail.com");
        assertThat(existing.getPhoneNumber()).isEqualTo("770000000");
        assertThat(existing.getRole()).isEqualTo(Roles.CASHIER);
        assertThat(existing.getSales()).isEqualTo(mappedSales);
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void updateCashier_keepsExistingFieldsWhenDtoFieldsNull() {
        /*Mockito's default answer for an unstubbed Boolean accessor is false, not null,
          so it needs to be stubbed explicitly to exercise the "field not provided" branch*/
        var dto = mock(CashierDTO.class);
        when(dto.active()).thenReturn(null);

        var existing = new Cashier();
        existing.setFirstname("Original");
        existing.setActive(true);
        var saved = new Cashier();
        var resultDTO = mock(CashierDTO.class);

        when(cashierRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(cashierRepository.saveAndFlush(existing)).thenReturn(saved);
        when(cashierMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = cashierService.updateCashier(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getFirstname()).isEqualTo("Original");
        assertThat(existing.isActive()).isTrue();
        verify(cashierMapper, never()).asEntity(any());
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
    void getCashiersByFilter_withAllNullParams_returnsPage() {
        var page = new PageImpl<>(List.of(new Cashier()));
        when(cashierRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(cashierMapper.asDTO(any(Cashier.class))).thenReturn(mock(CashierDTO.class));

        Page<CashierDTO> result = cashierService.getCashiersByFilter(
                null, null, null, null, null, null, null, null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getCashiersByFilter_withAllParamsSet_returnsPage() {
        var page = new PageImpl<>(List.of(new Cashier()));
        when(cashierRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(cashierMapper.asDTO(any(Cashier.class))).thenReturn(mock(CashierDTO.class));

        Page<CashierDTO> result = cashierService.getCashiersByFilter(
                1L, "REF", "John", "Doe", "jdoe", "jdoe@mail.com", "770000000",
                Roles.CASHIER, 2L, true,
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getCashiersByFilter_withEmptyStringParams_returnsPage() {
        var page = new PageImpl<>(List.of(new Cashier()));
        when(cashierRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(cashierMapper.asDTO(any(Cashier.class))).thenReturn(mock(CashierDTO.class));

        Page<CashierDTO> result = cashierService.getCashiersByFilter(
                null, "", "", "", "", "", "", null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getCashierById_returnsMappedDTOWhenFound() {
        var entity = new Cashier();
        var resultDTO = mock(CashierDTO.class);

        when(cashierRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(cashierMapper.asDTO(entity)).thenReturn(resultDTO);

        var result = cashierService.getCashierById(1L);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void getCashierById_throwsWhenNotFound() {
        when(cashierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cashierService.getCashierById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}