package com.sn.onepay.services.impl;

import com.sn.onepay.dto.CashierCreateDTO;
import com.sn.onepay.dto.CashierDTO;
import com.sn.onepay.dto.CashierUpdateDTO;
import com.sn.onepay.entity.Cashier;
import com.sn.onepay.entity.Sales;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.CashierMapper;
import com.sn.onepay.repository.CashierRepository;
import com.sn.onepay.repository.SalesRepository;
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
class CashierServiceImplTest {

    @Mock
    CashierRepository cashierRepository;

    @Mock
    SalesRepository salesRepository;

    @Mock
    CashierMapper cashierMapper;

    @InjectMocks
    CashierServiceImpl cashierService;

    @Test
    void createCashier_savesAndReturnsDTO() {
        var createDTO = mock(CashierCreateDTO.class);
        when(createDTO.firstname()).thenReturn("Jean");
        when(createDTO.lastname()).thenReturn("Dupont");
        when(createDTO.username()).thenReturn("jdupont");
        when(createDTO.email()).thenReturn("jean@mail.com");
        when(createDTO.phoneNumber()).thenReturn("770000000");
        when(createDTO.role()).thenReturn(Roles.CASHIER);
        when(createDTO.salesId()).thenReturn(2L);

        var sales = new Sales();
        var saved = new Cashier();
        var resultDTO = mock(CashierDTO.class);

        when(salesRepository.findById(2L)).thenReturn(Optional.of(sales));
        when(cashierRepository.save(any(Cashier.class))).thenReturn(saved);
        when(cashierMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = cashierService.createCashier(createDTO);

        assertThat(result).isEqualTo(resultDTO);

        ArgumentCaptor<Cashier> captor = ArgumentCaptor.forClass(Cashier.class);
        verify(cashierRepository).save(captor.capture());
        assertThat(captor.getValue().isActive()).isTrue();
        assertThat(captor.getValue().getRef()).isNotBlank();
        assertThat(captor.getValue().getFirstname()).isEqualTo("Jean");
        assertThat(captor.getValue().getLastname()).isEqualTo("Dupont");
        assertThat(captor.getValue().getUsername()).isEqualTo("jdupont");
        assertThat(captor.getValue().getEmail()).isEqualTo("jean@mail.com");
        assertThat(captor.getValue().getPhoneNumber()).isEqualTo("770000000");
        assertThat(captor.getValue().getRole()).isEqualTo(Roles.CASHIER);
        assertThat(captor.getValue().getSales()).isEqualTo(sales);
    }

    @Test
    void createCashier_throwsWhenSalesNotFound() {
        var createDTO = mock(CashierCreateDTO.class);
        when(createDTO.salesId()).thenReturn(99L);
        when(salesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cashierService.createCashier(createDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateCashier_throwsWhenNotFound() {
        when(cashierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cashierService.updateCashier(mock(CashierUpdateDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateCashier_updatesAllFieldsWhenProvided() {
        var dto = mock(CashierUpdateDTO.class);
        when(dto.firstname()).thenReturn("Jean");
        when(dto.lastname()).thenReturn("Dupont");
        when(dto.username()).thenReturn("jdupont");
        when(dto.email()).thenReturn("jean@mail.com");
        when(dto.phoneNumber()).thenReturn("770000000");
        when(dto.role()).thenReturn(Roles.CASHIER);
        when(dto.active()).thenReturn(true);

        var existing = new Cashier();
        var saved = new Cashier();
        var resultDTO = mock(CashierDTO.class);

        when(cashierRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(cashierRepository.saveAndFlush(existing)).thenReturn(saved);
        when(cashierMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = cashierService.updateCashier(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getFirstname()).isEqualTo("Jean");
        assertThat(existing.getLastname()).isEqualTo("Dupont");
        assertThat(existing.getUsername()).isEqualTo("jdupont");
        assertThat(existing.getEmail()).isEqualTo("jean@mail.com");
        assertThat(existing.getPhoneNumber()).isEqualTo("770000000");
        assertThat(existing.getRole()).isEqualTo(Roles.CASHIER);
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void updateCashier_keepsExistingFieldsWhenDtoFieldsNull() {
        /*Mockito's default answer for an unstubbed Boolean accessor is false, not null,
          so it needs to be stubbed explicitly to exercise the "field not provided" branch*/
        var dto = mock(CashierUpdateDTO.class);
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
