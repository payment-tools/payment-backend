package com.sn.onepay.services.impl;

import com.sn.onepay.dto.EnterpriseDTO;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.enumeration.Modules;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.EnterpriseMapper;
import com.sn.onepay.repository.EnterpriseRepository;
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
class EnterpriseServiceImplTest {

    @Mock
    EnterpriseRepository enterpriseRepository;

    @Mock
    EnterpriseMapper enterpriseMapper;

    @InjectMocks
    EnterpriseServiceImpl enterpriseService;

    @Test
    void createEnterprise_setsActualQuotaToZeroAndSaves() {
        var dto = mock(EnterpriseDTO.class);
        var entity = new Enterprise();
        var saved = new Enterprise();
        var resultDTO = mock(EnterpriseDTO.class);

        when(enterpriseMapper.asEntity(dto)).thenReturn(entity);
        when(enterpriseRepository.save(entity)).thenReturn(saved);
        when(enterpriseMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = enterpriseService.createEnterprise(dto);

        assertThat(entity.getActualQuota()).isEqualTo(0L);
        assertThat(result).isEqualTo(resultDTO);
        verify(enterpriseRepository).save(entity);
    }

    @Test
    void updateEnterprise_throwsWhenNotFound() {
        when(enterpriseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enterpriseService.updateEnterprise(mock(EnterpriseDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateEnterprise_savesWhenFound() {
        var dto = mock(EnterpriseDTO.class);
        var existing = new Enterprise();
        var updated = new Enterprise();
        var resultDTO = mock(EnterpriseDTO.class);

        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(enterpriseMapper.asEntity(dto)).thenReturn(updated);
        when(enterpriseRepository.saveAndFlush(updated)).thenReturn(updated);
        when(enterpriseMapper.asDTO(updated)).thenReturn(resultDTO);

        var result = enterpriseService.updateEnterprise(dto, 1L);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void deleteEnterprise_throwsWhenNotFound() {
        when(enterpriseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enterpriseService.deleteEnterprise(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteEnterprise_deletesWhenFound() {
        var existing = new Enterprise();
        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(enterpriseRepository).deleteById(1L);

        enterpriseService.deleteEnterprise(1L);

        verify(enterpriseRepository).deleteById(1L);
    }

    @Test
    void getEnterprisesByFilters_withAllNullParams_returnsPage() {
        var entityPage = new PageImpl<>(List.of(new Enterprise()));
        when(enterpriseRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(entityPage);
        when(enterpriseMapper.asDTO(any(Enterprise.class))).thenReturn(mock(EnterpriseDTO.class));

        Page<EnterpriseDTO> result = enterpriseService.getEnterprisesByFilters(
                null, null, null, null, null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getEnterprisesByFilters_withAllParamsSet_returnsPage() {
        var entityPage = new PageImpl<>(List.of(new Enterprise()));
        when(enterpriseRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(entityPage);
        when(enterpriseMapper.asDTO(any(Enterprise.class))).thenReturn(mock(EnterpriseDTO.class));

        Page<EnterpriseDTO> result = enterpriseService.getEnterprisesByFilters(
                1L, "REF01", "MyEnterprise", 100L, 50L, "Dakar",
                Modules.RESTAURATION, LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }
}