package com.sn.onepay.services.impl;

import com.sn.onepay.dto.PartnershipDTO;
import com.sn.onepay.dto.SubventionDTO;
import com.sn.onepay.entity.Subvention;
import com.sn.onepay.enumeration.StateStatus;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.SubventionMapper;
import com.sn.onepay.repository.SubventionRepository;
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
class SubventionServiceImplTest {

    @Mock
    SubventionRepository subventionRepository;

    @Mock
    SubventionMapper subventionMapper;

    @InjectMocks
    SubventionServiceImpl subventionService;

    private SubventionDTO buildDTO(Double emp, Double employer, PartnershipDTO partnership) {
        var dto = mock(SubventionDTO.class);
        when(dto.employeePercent()).thenReturn(emp);
        when(dto.employerPercent()).thenReturn(employer);
        when(dto.partnership()).thenReturn(partnership);
        return dto;
    }

    @Test
    void createSubvention_throwsWhenPercentagesNotEqualTo100() {
        var dto = mock(SubventionDTO.class);
        when(dto.employeePercent()).thenReturn(40.0);
        when(dto.employerPercent()).thenReturn(40.0);

        assertThatThrownBy(() -> subventionService.createSubvention(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("100");
    }

    @Test
    void createSubvention_throwsWhenPartnershipIsNull() {
        var dto = buildDTO(60.0, 40.0, null);

        assertThatThrownBy(() -> subventionService.createSubvention(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("partenariat");
    }

    @Test
    void createSubvention_throwsWhenPartnershipNotActive() {
        var partnership = mock(PartnershipDTO.class);
        when(partnership.status()).thenReturn(StateStatus.INACTIVE);

        var dto = buildDTO(60.0, 40.0, partnership);

        assertThatThrownBy(() -> subventionService.createSubvention(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("partenariat");
    }

    @Test
    void createSubvention_happyPath() {
        var partnership = mock(PartnershipDTO.class);
        when(partnership.status()).thenReturn(StateStatus.ACTIVE);

        var dto = buildDTO(60.0, 40.0, partnership);
        var entity = new Subvention();
        var saved = new Subvention();
        var resultDTO = mock(SubventionDTO.class);

        when(subventionMapper.asEntity(dto)).thenReturn(entity);
        when(subventionRepository.save(entity)).thenReturn(saved);
        when(subventionMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = subventionService.createSubvention(dto);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void updateSubvention_throwsWhenNotFound() {
        when(subventionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subventionService.updateSubvention(mock(SubventionDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateSubvention_modifiesFieldsAndSaves() {
        var dto = mock(SubventionDTO.class);
        when(dto.employeePercent()).thenReturn(70.0);
        when(dto.employerPercent()).thenReturn(30.0);
        when(dto.status()).thenReturn(StateStatus.INACTIVE);

        var existing = new Subvention();
        var updated = new Subvention();
        var resultDTO = mock(SubventionDTO.class);

        when(subventionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(subventionRepository.saveAndFlush(existing)).thenReturn(updated);
        when(subventionMapper.asDTO(updated)).thenReturn(resultDTO);

        var result = subventionService.updateSubvention(dto, 1L);

        assertThat(existing.getEmployeePercent()).isEqualTo(70.0);
        assertThat(existing.getEmployerPercent()).isEqualTo(30.0);
        assertThat(existing.getStatus()).isEqualTo(StateStatus.INACTIVE);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void updateSubvention_withNullFields_doesNotChangeFields() {
        var dto = mock(SubventionDTO.class);
        when(dto.employeePercent()).thenReturn(null);
        when(dto.employerPercent()).thenReturn(null);
        when(dto.status()).thenReturn(null);

        var existing = new Subvention();
        existing.setEmployeePercent(60.0);
        existing.setEmployerPercent(40.0);
        existing.setStatus(StateStatus.ACTIVE);

        var updated = new Subvention();
        var resultDTO = mock(SubventionDTO.class);

        when(subventionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(subventionRepository.saveAndFlush(existing)).thenReturn(updated);
        when(subventionMapper.asDTO(updated)).thenReturn(resultDTO);

        subventionService.updateSubvention(dto, 1L);

        assertThat(existing.getEmployeePercent()).isEqualTo(60.0);
        assertThat(existing.getEmployerPercent()).isEqualTo(40.0);
        assertThat(existing.getStatus()).isEqualTo(StateStatus.ACTIVE);
    }

    @Test
    void deleteSubvention_throwsWhenNotFound() {
        when(subventionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subventionService.deleteSubvention(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteSubvention_setsStatusToInactive() {
        var subvention = new Subvention();
        when(subventionRepository.findById(1L)).thenReturn(Optional.of(subvention));
        when(subventionRepository.saveAndFlush(subvention)).thenReturn(subvention);

        subventionService.deleteSubvention(1L);

        assertThat(subvention.getStatus()).isEqualTo(StateStatus.INACTIVE);
        verify(subventionRepository).saveAndFlush(subvention);
    }

    @Test
    void getSubventionsByFilters_withNullParams_returnsPage() {
        var page = new PageImpl<>(List.of(new Subvention()));
        when(subventionRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(subventionMapper.asDTO(any(Subvention.class))).thenReturn(mock(SubventionDTO.class));

        Page<SubventionDTO> result = subventionService.getSubventionsByFilters(
                null, null, null, null, null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getSubventionsByFilters_withAllParamsSet_returnsPage() {
        var page = new PageImpl<>(List.of(new Subvention()));
        when(subventionRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(subventionMapper.asDTO(any(Subvention.class))).thenReturn(mock(SubventionDTO.class));

        Page<SubventionDTO> result = subventionService.getSubventionsByFilters(
                1L, "REF", 60.0, 40.0, 2L, 3L, StateStatus.ACTIVE,
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }
}