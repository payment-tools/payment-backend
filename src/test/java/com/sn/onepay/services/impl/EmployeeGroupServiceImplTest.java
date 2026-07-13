package com.sn.onepay.services.impl;

import com.sn.onepay.dto.EmployeeGroupCreateDTO;
import com.sn.onepay.dto.EmployeeGroupDTO;
import com.sn.onepay.dto.EmployeeGroupUpdateDTO;
import com.sn.onepay.entity.Client;
import com.sn.onepay.entity.EmployeeGroup;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.EmployeeGroupMapper;
import com.sn.onepay.repository.ClientRepository;
import com.sn.onepay.repository.EmployeeGroupRepository;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeGroupServiceImplTest {

    @Mock
    EmployeeGroupRepository employeeGroupRepository;

    @Mock
    EnterpriseRepository enterpriseRepository;

    @Mock
    ClientRepository clientRepository;

    @Mock
    EmployeeGroupMapper employeeGroupMapper;

    @InjectMocks
    EmployeeGroupServiceImpl employeeGroupService;

    private Enterprise enterprise(Long id) {
        var enterprise = new Enterprise();
        enterprise.setId(id);
        return enterprise;
    }

    private Client client(boolean active, Enterprise enterprise) {
        var client = new Client();
        client.setActive(active);
        client.setEnterprise(enterprise);
        return client;
    }

    @Test
    void createEmployeeGroup_throwsWhenEnterpriseNotFound() {
        when(enterpriseRepository.findById(1L)).thenReturn(Optional.empty());

        var dto = new EmployeeGroupCreateDTO("RH", 1L, null);

        assertThatThrownBy(() -> employeeGroupService.createEmployeeGroup(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Enterprise");
    }

    @Test
    void createEmployeeGroup_throwsWhenClientNotFound() {
        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(enterprise(1L)));
        when(clientRepository.findById(5L)).thenReturn(Optional.empty());

        var dto = new EmployeeGroupCreateDTO("RH", 1L, List.of(5L));

        assertThatThrownBy(() -> employeeGroupService.createEmployeeGroup(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Client");
    }

    @Test
    void createEmployeeGroup_throwsWhenClientNotActive() {
        var enterprise = enterprise(1L);
        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(enterprise));
        when(clientRepository.findById(5L)).thenReturn(Optional.of(client(false, enterprise)));

        var dto = new EmployeeGroupCreateDTO("RH", 1L, List.of(5L));

        assertThatThrownBy(() -> employeeGroupService.createEmployeeGroup(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("inactif");
    }

    @Test
    void createEmployeeGroup_throwsWhenClientHasNoEnterprise() {
        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(enterprise(1L)));
        when(clientRepository.findById(5L)).thenReturn(Optional.of(client(true, null)));

        var dto = new EmployeeGroupCreateDTO("RH", 1L, List.of(5L));

        assertThatThrownBy(() -> employeeGroupService.createEmployeeGroup(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("n'appartient pas");
    }

    @Test
    void createEmployeeGroup_throwsWhenClientBelongsToAnotherEnterprise() {
        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(enterprise(1L)));
        when(clientRepository.findById(5L)).thenReturn(Optional.of(client(true, enterprise(2L))));

        var dto = new EmployeeGroupCreateDTO("RH", 1L, List.of(5L));

        assertThatThrownBy(() -> employeeGroupService.createEmployeeGroup(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("n'appartient pas");
    }

    @Test
    void createEmployeeGroup_withoutClients_savesAndReturnsDTO() {
        var enterprise = enterprise(1L);
        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(enterprise));

        var saved = new EmployeeGroup();
        var resultDTO = mock(EmployeeGroupDTO.class);
        when(employeeGroupRepository.save(any(EmployeeGroup.class))).thenAnswer(invocation -> {
            EmployeeGroup toSave = invocation.getArgument(0);
            assertThat(toSave.getRef()).isNotNull();
            assertThat(toSave.getName()).isEqualTo("RH");
            assertThat(toSave.getEnterprise()).isEqualTo(enterprise);
            assertThat(toSave.getClients()).isNull();
            assertThat(toSave.isActive()).isTrue();
            return saved;
        });
        when(employeeGroupMapper.asDTO(saved)).thenReturn(resultDTO);

        var dto = new EmployeeGroupCreateDTO("RH", 1L, null);
        var result = employeeGroupService.createEmployeeGroup(dto);

        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void createEmployeeGroup_withClients_savesAndReturnsDTO() {
        var enterprise = enterprise(1L);
        var client = client(true, enterprise);
        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(enterprise));
        when(clientRepository.findById(5L)).thenReturn(Optional.of(client));

        var saved = new EmployeeGroup();
        var resultDTO = mock(EmployeeGroupDTO.class);
        when(employeeGroupRepository.save(any(EmployeeGroup.class))).thenAnswer(invocation -> {
            EmployeeGroup toSave = invocation.getArgument(0);
            assertThat(toSave.getClients()).containsExactly(client);
            return saved;
        });
        when(employeeGroupMapper.asDTO(saved)).thenReturn(resultDTO);

        var dto = new EmployeeGroupCreateDTO("RH", 1L, List.of(5L));
        var result = employeeGroupService.createEmployeeGroup(dto);

        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void updateEmployeeGroup_throwsWhenNotFound() {
        when(employeeGroupRepository.findById(99L)).thenReturn(Optional.empty());

        var dto = new EmployeeGroupUpdateDTO(null, null, null);

        assertThatThrownBy(() -> employeeGroupService.updateEmployeeGroup(dto, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateEmployeeGroup_modifiesFieldsAndSaves() {
        var enterprise = enterprise(1L);
        var client = client(true, enterprise);
        var existing = new EmployeeGroup();
        existing.setName("Old Name");
        existing.setEnterprise(enterprise);
        existing.setActive(true);

        var updated = new EmployeeGroup();
        var resultDTO = mock(EmployeeGroupDTO.class);

        when(employeeGroupRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clientRepository.findById(5L)).thenReturn(Optional.of(client));
        when(employeeGroupRepository.saveAndFlush(existing)).thenReturn(updated);
        when(employeeGroupMapper.asDTO(updated)).thenReturn(resultDTO);

        var dto = new EmployeeGroupUpdateDTO("New Name", List.of(5L), false);
        var result = employeeGroupService.updateEmployeeGroup(dto, 1L);

        assertThat(existing.getName()).isEqualTo("New Name");
        assertThat(existing.getClients()).containsExactly(client);
        assertThat(existing.isActive()).isFalse();
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void updateEmployeeGroup_withNullFields_doesNotChangeFields() {
        var existing = new EmployeeGroup();
        existing.setName("Old Name");
        existing.setActive(true);

        var updated = new EmployeeGroup();
        var resultDTO = mock(EmployeeGroupDTO.class);

        when(employeeGroupRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(employeeGroupRepository.saveAndFlush(existing)).thenReturn(updated);
        when(employeeGroupMapper.asDTO(updated)).thenReturn(resultDTO);

        var dto = new EmployeeGroupUpdateDTO(null, null, null);
        employeeGroupService.updateEmployeeGroup(dto, 1L);

        assertThat(existing.getName()).isEqualTo("Old Name");
        assertThat(existing.getClients()).isNull();
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void updateEmployeeGroup_throwsWhenGroupHasNoEnterpriseAndClientsProvided() {
        var existing = new EmployeeGroup();
        existing.setEnterprise(null);

        when(employeeGroupRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clientRepository.findById(5L)).thenReturn(Optional.of(client(true, enterprise(1L))));

        var dto = new EmployeeGroupUpdateDTO(null, List.of(5L), null);

        assertThatThrownBy(() -> employeeGroupService.updateEmployeeGroup(dto, 1L))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("n'appartient pas");
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
    void getEmployeeGroupsByFilters_withEmptyRefAndName_ignoresThoseFilters() {
        var page = new PageImpl<>(List.of(new EmployeeGroup()));
        when(employeeGroupRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(employeeGroupMapper.asDTO(any(EmployeeGroup.class))).thenReturn(mock(EmployeeGroupDTO.class));

        Page<EmployeeGroupDTO> result = employeeGroupService.getEmployeeGroupsByFilters(
                null, "", "", null, null, null, null, Pageable.unpaged());

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
