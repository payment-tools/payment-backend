package com.sn.onepay.services.impl;

import com.sn.onepay.dto.SalesProfileDTO;
import com.sn.onepay.entity.Sales;
import com.sn.onepay.entity.SalesProfile;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.SalesProfileMapper;
import com.sn.onepay.repository.SalesProfileRepository;
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
class SalesProfileServiceImplTest {

    @Mock
    SalesProfileRepository salesProfileRepository;

    @Mock
    SalesProfileMapper salesProfileMapper;

    @InjectMocks
    SalesProfileServiceImpl salesProfileService;

    @Test
    void createSalesProfile_savesAndReturnsDTO() {
        var dto = mock(SalesProfileDTO.class);
        var entity = new SalesProfile();
        var saved = new SalesProfile();
        var resultDTO = mock(SalesProfileDTO.class);

        when(salesProfileMapper.asEntity(dto)).thenReturn(entity);
        when(salesProfileRepository.save(any(SalesProfile.class))).thenReturn(saved);
        when(salesProfileMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = salesProfileService.createSalesProfile(dto);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(entity.isActive()).isTrue();
    }

    @Test
    void updateSalesProfile_throwsWhenNotFound() {
        when(salesProfileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salesProfileService.updateSalesProfile(mock(SalesProfileDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateSalesProfile_updatesAllFieldsWhenProvided() {
        var dto = mock(SalesProfileDTO.class);
        when(dto.ref()).thenReturn("REF1");
        when(dto.firstname()).thenReturn("Jean");
        when(dto.lastname()).thenReturn("Dupont");
        when(dto.username()).thenReturn("jdupont");
        when(dto.email()).thenReturn("jean@mail.com");
        when(dto.phoneNumber()).thenReturn("770000000");
        when(dto.role()).thenReturn(Roles.SALES_ADMIN);
        var sales = new Sales();
        when(dto.sales()).thenReturn(sales);
        when(dto.active()).thenReturn(true);

        var existing = new SalesProfile();
        var saved = new SalesProfile();
        var resultDTO = mock(SalesProfileDTO.class);

        when(salesProfileRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(salesProfileRepository.saveAndFlush(existing)).thenReturn(saved);
        when(salesProfileMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = salesProfileService.updateSalesProfile(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getRef()).isEqualTo("REF1");
        assertThat(existing.getFirstname()).isEqualTo("Jean");
        assertThat(existing.getLastname()).isEqualTo("Dupont");
        assertThat(existing.getUsername()).isEqualTo("jdupont");
        assertThat(existing.getEmail()).isEqualTo("jean@mail.com");
        assertThat(existing.getPhoneNumber()).isEqualTo("770000000");
        assertThat(existing.getRole()).isEqualTo(Roles.SALES_ADMIN);
        assertThat(existing.getSales()).isEqualTo(sales);
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void updateSalesProfile_keepsExistingFieldsWhenDtoFieldsNull() {
        /*Mockito's default answer for an unstubbed Boolean accessor is false, not null,
          so it needs to be stubbed explicitly to exercise the "field not provided" branch*/
        var dto = mock(SalesProfileDTO.class);
        when(dto.active()).thenReturn(null);

        var existing = new SalesProfile();
        existing.setFirstname("Original");
        existing.setActive(true);
        var saved = new SalesProfile();
        var resultDTO = mock(SalesProfileDTO.class);

        when(salesProfileRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(salesProfileRepository.saveAndFlush(existing)).thenReturn(saved);
        when(salesProfileMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = salesProfileService.updateSalesProfile(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getFirstname()).isEqualTo("Original");
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void deleteSalesProfile_throwsWhenNotFound() {
        when(salesProfileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salesProfileService.deleteSalesProfile(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteSalesProfile_setsActiveToFalse() {
        var profile = new SalesProfile();
        when(salesProfileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(salesProfileRepository.saveAndFlush(profile)).thenReturn(profile);

        salesProfileService.deleteSalesProfile(1L);

        assertThat(profile.isActive()).isFalse();
        verify(salesProfileRepository).saveAndFlush(profile);
    }

    @Test
    void getSalesProfilesByFilters_withNullParams_returnsPage() {
        var page = new PageImpl<>(List.of(new SalesProfile()));
        when(salesProfileRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(salesProfileMapper.asDTO(any(SalesProfile.class))).thenReturn(mock(SalesProfileDTO.class));

        Page<SalesProfileDTO> result = salesProfileService.getSalesProfilesByFilters(
                null, null, null, null, null, null, null, null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getSalesProfilesByFilters_withAllParamsSet_returnsPage() {
        var page = new PageImpl<>(List.of(new SalesProfile()));
        when(salesProfileRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(salesProfileMapper.asDTO(any(SalesProfile.class))).thenReturn(mock(SalesProfileDTO.class));

        Page<SalesProfileDTO> result = salesProfileService.getSalesProfilesByFilters(
                1L, "REF", "John", "Doe", "jdoe", "jdoe@mail.com", "770000000",
                Roles.SALES_ADMIN, 2L, true,
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }
}