package com.sn.onepay.services.impl;

import com.sn.onepay.dto.EmployeeGroupDTO;
import com.sn.onepay.entity.EmployeeGroup;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.EmployeeGroupMapper;
import com.sn.onepay.repository.EmployeeGroupRepository;
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
class EmployeeGroupServiceImplTest {

    @Mock
    EmployeeGroupRepository employeeGroupRepository;

    @Mock
    EmployeeGroupMapper employeeGroupMapper;

    @InjectMocks
    EmployeeGroupServiceImpl employeeGroupService;

    @Test
    void createEmployeeGroup_savesAndReturnsDTO() {
        var dto = mock(EmployeeGroupDTO.class);
        var entity = new EmployeeGroup();
        var saved = new EmployeeGroup();
        var resultDTO = mock(EmployeeGroupDTO.class);

        when(employeeGroupMapper.asEntity(dto)).thenReturn(entity);
        when(employeeGroupRepository.save(any(EmployeeGroup.class))).thenReturn(saved);
        when(employeeGroupMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = employeeGroupService.createEmployeeGroup(dto);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(entity.isActive()).isTrue();
    }

    @Test
    void updateEmployeeGroup_throwsWhenNotFound() {
        when(employeeGroupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeGroupService.updateEmployeeGroup(mock(EmployeeGroupDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateEmployeeGroup_modifiesFieldsAndSaves() {
        var dto = mock(EmployeeGroupDTO.class);
        when(dto.name()).thenReturn("New Name");
        when(dto.active()).thenReturn(false);

        var existing = new EmployeeGroup();
        existing.setName("Old Name");
        existing.setActive(true);

        var updated = new EmployeeGroup();
        var resultDTO = mock(EmployeeGroupDTO.class);

        when(employeeGroupRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(employeeGroupRepository.saveAndFlush(existing)).thenReturn(updated);
        when(employeeGroupMapper.asDTO(updated)).thenReturn(resultDTO);

        var result = employeeGroupService.updateEmployeeGroup(dto, 1L);

        assertThat(existing.getName()).isEqualTo("New Name");
        assertThat(existing.isActive()).isFalse();
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void updateEmployeeGroup_withNullActive_doesNotChangeActive() {
        var dto = mock(EmployeeGroupDTO.class);
        when(dto.name()).thenReturn("Updated Name");
        when(dto.active()).thenReturn(null);

        var existing = new EmployeeGroup();
        existing.setName("Old Name");
        existing.setActive(true);

        var updated = new EmployeeGroup();
        var resultDTO = mock(EmployeeGroupDTO.class);

        when(employeeGroupRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(employeeGroupRepository.saveAndFlush(existing)).thenReturn(updated);
        when(employeeGroupMapper.asDTO(updated)).thenReturn(resultDTO);

        employeeGroupService.updateEmployeeGroup(dto, 1L);

        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void deleteEmployeeGroup_throwsWhenNotFound() {
        when(employeeGroupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeGroupService.deleteEmployeeGroup(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteEmployeeGroup_setsActiveToFalse() {
        var group = new EmployeeGroup();
        when(employeeGroupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(employeeGroupRepository.saveAndFlush(group)).thenReturn(group);

        employeeGroupService.deleteEmployeeGroup(1L);

        assertThat(group.isActive()).isFalse();
        verify(employeeGroupRepository).saveAndFlush(group);
    }

    @Test
    void getEmployeeGroupsByFilters_withNullParams_returnsPage() {
        var page = new PageImpl<>(List.of(new EmployeeGroup()));
        when(employeeGroupRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(employeeGroupMapper.asDTO(any(EmployeeGroup.class))).thenReturn(mock(EmployeeGroupDTO.class));

        Page<EmployeeGroupDTO> result = employeeGroupService.getEmployeeGroupsByFilters(
                null, null, null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getEmployeeGroupsByFilters_withAllParamsSet_returnsPage() {
        var page = new PageImpl<>(List.of(new EmployeeGroup()));
        when(employeeGroupRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(employeeGroupMapper.asDTO(any(EmployeeGroup.class))).thenReturn(mock(EmployeeGroupDTO.class));

        Page<EmployeeGroupDTO> result = employeeGroupService.getEmployeeGroupsByFilters(
                1L, "REF", "RH Group", 2L, true,
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }
}