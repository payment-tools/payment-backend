package com.sn.onepay.services.impl;

import com.sn.onepay.dto.SalesProfileDTO;
import com.sn.onepay.entity.SalesProfile;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.enumeration.StateStatus;
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
        when(salesProfileRepository.save(entity)).thenReturn(saved);
        when(salesProfileMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = salesProfileService.createSalesProfile(dto);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void updateSalesProfile_throwsWhenNotFound() {
        when(salesProfileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salesProfileService.updateSalesProfile(mock(SalesProfileDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateSalesProfile_savesWhenFound() {
        var dto = mock(SalesProfileDTO.class);
        var existing = new SalesProfile();
        var updated = new SalesProfile();
        var resultDTO = mock(SalesProfileDTO.class);

        when(salesProfileRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(salesProfileMapper.asEntity(dto)).thenReturn(updated);
        when(salesProfileRepository.save(updated)).thenReturn(updated);
        when(salesProfileMapper.asDTO(updated)).thenReturn(resultDTO);

        var result = salesProfileService.updateSalesProfile(dto, 1L);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void deleteSalesProfile_throwsWhenNotFound() {
        when(salesProfileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salesProfileService.deleteSalesProfile(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteSalesProfile_deletesWhenFound() {
        when(salesProfileRepository.findById(1L)).thenReturn(Optional.of(new SalesProfile()));
        doNothing().when(salesProfileRepository).deleteById(1L);

        salesProfileService.deleteSalesProfile(1L);

        verify(salesProfileRepository).deleteById(1L);
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
                Roles.SALES_ADMIN, 2L, StateStatus.ACTIVE,
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }
}