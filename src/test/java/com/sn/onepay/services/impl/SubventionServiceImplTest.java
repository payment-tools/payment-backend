package com.sn.onepay.services.impl;

import com.sn.onepay.dto.SubventionCreateDTO;
import com.sn.onepay.dto.SubventionDTO;
import com.sn.onepay.dto.SubventionUpdateDTO;
import com.sn.onepay.entity.EmployeeGroup;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.entity.Partnership;
import com.sn.onepay.entity.Subvention;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.SubventionMapper;
import com.sn.onepay.repository.EmployeeGroupRepository;
import com.sn.onepay.repository.PartnershipRepository;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubventionServiceImplTest {

    @Mock
    SubventionRepository subventionRepository;

    @Mock
    PartnershipRepository partnershipRepository;

    @Mock
    EmployeeGroupRepository employeeGroupRepository;

    @Mock
    SubventionMapper subventionMapper;

    @InjectMocks
    SubventionServiceImpl subventionService;

    private Enterprise enterprise(Long id) {
        var enterprise = new Enterprise();
        enterprise.setId(id);
        return enterprise;
    }

    private Partnership partnership(boolean active, Enterprise enterprise) {
        var partnership = new Partnership();
        partnership.setActive(active);
        partnership.setEnterprise(enterprise);
        return partnership;
    }

    private EmployeeGroup employeeGroup(boolean active, Enterprise enterprise) {
        var group = new EmployeeGroup();
        group.setActive(active);
        group.setEnterprise(enterprise);
        return group;
    }

    @Test
    void createSubvention_throwsWhenEmployeePercentIsNull() {
        var dto = new SubventionCreateDTO(null, 40.0, 1L, 2L);

        assertThatThrownBy(() -> subventionService.createSubvention(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("ensemble");
    }

    @Test
    void createSubvention_throwsWhenEmployerPercentIsNull() {
        var dto = new SubventionCreateDTO(60.0, null, 1L, 2L);

        assertThatThrownBy(() -> subventionService.createSubvention(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("ensemble");
    }

    @Test
    void createSubvention_throwsWhenPercentagesNotEqualTo100() {
        var dto = new SubventionCreateDTO(40.0, 40.0, 1L, 2L);

        assertThatThrownBy(() -> subventionService.createSubvention(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("100");
    }

    @Test
    void createSubvention_throwsWhenPartnershipNotFound() {
        when(partnershipRepository.findById(1L)).thenReturn(Optional.empty());

        var dto = new SubventionCreateDTO(60.0, 40.0, 1L, 2L);

        assertThatThrownBy(() -> subventionService.createSubvention(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Partnership");
    }

    @Test
    void createSubvention_throwsWhenPartnershipNotActive() {
        when(partnershipRepository.findById(1L)).thenReturn(Optional.of(partnership(false, enterprise(10L))));

        var dto = new SubventionCreateDTO(60.0, 40.0, 1L, 2L);

        assertThatThrownBy(() -> subventionService.createSubvention(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("partenariat");
    }

    @Test
    void createSubvention_throwsWhenEmployeeGroupNotFound() {
        when(partnershipRepository.findById(1L)).thenReturn(Optional.of(partnership(true, enterprise(10L))));
        when(employeeGroupRepository.findById(2L)).thenReturn(Optional.empty());

        var dto = new SubventionCreateDTO(60.0, 40.0, 1L, 2L);

        assertThatThrownBy(() -> subventionService.createSubvention(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("EmployeeGroup");
    }

    @Test
    void createSubvention_throwsWhenEmployeeGroupNotActive() {
        when(partnershipRepository.findById(1L)).thenReturn(Optional.of(partnership(true, enterprise(10L))));
        when(employeeGroupRepository.findById(2L)).thenReturn(Optional.of(employeeGroup(false, enterprise(10L))));

        var dto = new SubventionCreateDTO(60.0, 40.0, 1L, 2L);

        assertThatThrownBy(() -> subventionService.createSubvention(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("groupe de collaborateurs actif");
    }

    @Test
    void createSubvention_throwsWhenEmployeeGroupHasNoEnterprise() {
        when(partnershipRepository.findById(1L)).thenReturn(Optional.of(partnership(true, enterprise(10L))));
        when(employeeGroupRepository.findById(2L)).thenReturn(Optional.of(employeeGroup(true, null)));

        var dto = new SubventionCreateDTO(60.0, 40.0, 1L, 2L);

        assertThatThrownBy(() -> subventionService.createSubvention(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("entreprise du partenariat");
    }

    @Test
    void createSubvention_throwsWhenPartnershipHasNoEnterprise() {
        when(partnershipRepository.findById(1L)).thenReturn(Optional.of(partnership(true, null)));
        when(employeeGroupRepository.findById(2L)).thenReturn(Optional.of(employeeGroup(true, enterprise(10L))));

        var dto = new SubventionCreateDTO(60.0, 40.0, 1L, 2L);

        assertThatThrownBy(() -> subventionService.createSubvention(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("entreprise du partenariat");
    }

    @Test
    void createSubvention_throwsWhenEnterprisesDoNotMatch() {
        when(partnershipRepository.findById(1L)).thenReturn(Optional.of(partnership(true, enterprise(10L))));
        when(employeeGroupRepository.findById(2L)).thenReturn(Optional.of(employeeGroup(true, enterprise(20L))));

        var dto = new SubventionCreateDTO(60.0, 40.0, 1L, 2L);

        assertThatThrownBy(() -> subventionService.createSubvention(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("entreprise du partenariat");
    }

    @Test
    void createSubvention_happyPath() {
        var partnership = partnership(true, enterprise(10L));
        var group = employeeGroup(true, enterprise(10L));
        when(partnershipRepository.findById(1L)).thenReturn(Optional.of(partnership));
        when(employeeGroupRepository.findById(2L)).thenReturn(Optional.of(group));

        var saved = new Subvention();
        var resultDTO = mock(SubventionDTO.class);
        when(subventionRepository.save(any(Subvention.class))).thenAnswer(invocation -> {
            Subvention toSave = invocation.getArgument(0);
            assertThat(toSave.getRef()).isNotNull();
            assertThat(toSave.getEmployeePercent()).isEqualTo(60.0);
            assertThat(toSave.getEmployerPercent()).isEqualTo(40.0);
            assertThat(toSave.getPartnership()).isEqualTo(partnership);
            assertThat(toSave.getEmployeeGroup()).isEqualTo(group);
            assertThat(toSave.isActive()).isTrue();
            return saved;
        });
        when(subventionMapper.asDTO(saved)).thenReturn(resultDTO);

        var dto = new SubventionCreateDTO(60.0, 40.0, 1L, 2L);
        var result = subventionService.createSubvention(dto);

        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void updateSubvention_throwsWhenNotFound() {
        when(subventionRepository.findById(99L)).thenReturn(Optional.empty());

        var dto = new SubventionUpdateDTO(null, null, null);

        assertThatThrownBy(() -> subventionService.updateSubvention(dto, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateSubvention_throwsWhenOnlyEmployeePercentProvided() {
        when(subventionRepository.findById(1L)).thenReturn(Optional.of(new Subvention()));

        var dto = new SubventionUpdateDTO(60.0, null, null);

        assertThatThrownBy(() -> subventionService.updateSubvention(dto, 1L))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("ensemble");
    }

    @Test
    void updateSubvention_throwsWhenOnlyEmployerPercentProvided() {
        when(subventionRepository.findById(1L)).thenReturn(Optional.of(new Subvention()));

        var dto = new SubventionUpdateDTO(null, 40.0, null);

        assertThatThrownBy(() -> subventionService.updateSubvention(dto, 1L))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("ensemble");
    }

    @Test
    void updateSubvention_throwsWhenPercentagesNotEqualTo100() {
        when(subventionRepository.findById(1L)).thenReturn(Optional.of(new Subvention()));

        var dto = new SubventionUpdateDTO(70.0, 40.0, null);

        assertThatThrownBy(() -> subventionService.updateSubvention(dto, 1L))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("100");
    }

    @Test
    void updateSubvention_modifiesFieldsAndSaves() {
        var existing = new Subvention();
        existing.setActive(true);
        var updated = new Subvention();
        var resultDTO = mock(SubventionDTO.class);

        when(subventionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(subventionRepository.saveAndFlush(existing)).thenReturn(updated);
        when(subventionMapper.asDTO(updated)).thenReturn(resultDTO);

        var dto = new SubventionUpdateDTO(70.0, 30.0, false);
        var result = subventionService.updateSubvention(dto, 1L);

        assertThat(existing.getEmployeePercent()).isEqualTo(70.0);
        assertThat(existing.getEmployerPercent()).isEqualTo(30.0);
        assertThat(existing.isActive()).isFalse();
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void updateSubvention_withNullFields_doesNotChangeFields() {
        var existing = new Subvention();
        existing.setEmployeePercent(60.0);
        existing.setEmployerPercent(40.0);
        existing.setActive(true);

        var updated = new Subvention();
        var resultDTO = mock(SubventionDTO.class);

        when(subventionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(subventionRepository.saveAndFlush(existing)).thenReturn(updated);
        when(subventionMapper.asDTO(updated)).thenReturn(resultDTO);

        var dto = new SubventionUpdateDTO(null, null, null);
        subventionService.updateSubvention(dto, 1L);

        assertThat(existing.getEmployeePercent()).isEqualTo(60.0);
        assertThat(existing.getEmployerPercent()).isEqualTo(40.0);
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void deleteSubvention_throwsWhenNotFound() {
        when(subventionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subventionService.deleteSubvention(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteSubvention_setsActiveToFalse() {
        var subvention = new Subvention();
        when(subventionRepository.findById(1L)).thenReturn(Optional.of(subvention));
        when(subventionRepository.saveAndFlush(subvention)).thenReturn(subvention);

        subventionService.deleteSubvention(1L);

        assertThat(subvention.isActive()).isFalse();
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
    void getSubventionsByFilters_withEmptyRef_ignoresRefFilter() {
        var page = new PageImpl<>(List.of(new Subvention()));
        when(subventionRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(subventionMapper.asDTO(any(Subvention.class))).thenReturn(mock(SubventionDTO.class));

        Page<SubventionDTO> result = subventionService.getSubventionsByFilters(
                null, "", null, null, null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getSubventionsByFilters_withAllParamsSet_returnsPage() {
        var page = new PageImpl<>(List.of(new Subvention()));
        when(subventionRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(subventionMapper.asDTO(any(Subvention.class))).thenReturn(mock(SubventionDTO.class));

        Page<SubventionDTO> result = subventionService.getSubventionsByFilters(
                1L, "REF", 60.0, 40.0, 2L, 3L, true,
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }
}
