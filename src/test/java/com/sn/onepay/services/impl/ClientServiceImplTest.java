package com.sn.onepay.services.impl;

import com.sn.onepay.dto.ClientCreateDTO;
import com.sn.onepay.dto.ClientDTO;
import com.sn.onepay.dto.ClientUpdateDTO;
import com.sn.onepay.entity.Client;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.ClientMapper;
import com.sn.onepay.repository.ClientRepository;
import com.sn.onepay.repository.EnterpriseRepository;
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
class ClientServiceImplTest {

    @Mock
    ClientRepository clientRepository;

    @Mock
    EnterpriseRepository enterpriseRepository;

    @Mock
    ClientMapper clientMapper;

    @InjectMocks
    ClientServiceImpl clientService;

    @Test
    void createClient_savesAndReturnsDTO() {
        var createDTO = mock(ClientCreateDTO.class);
        when(createDTO.firstname()).thenReturn("Jean");
        when(createDTO.lastname()).thenReturn("Dupont");
        when(createDTO.username()).thenReturn("jdupont");
        when(createDTO.email()).thenReturn("jean@mail.com");
        when(createDTO.phoneNumber()).thenReturn("770000000");
        when(createDTO.role()).thenReturn(Roles.CLIENT);
        when(createDTO.enterpriseId()).thenReturn(2L);

        var enterprise = new Enterprise();
        var saved = new Client();
        var resultDTO = mock(ClientDTO.class);

        when(enterpriseRepository.findById(2L)).thenReturn(Optional.of(enterprise));
        when(clientRepository.save(any(Client.class))).thenReturn(saved);
        when(clientMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = clientService.createClient(createDTO);

        assertThat(result).isEqualTo(resultDTO);

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(captor.capture());
        assertThat(captor.getValue().isActive()).isTrue();
        assertThat(captor.getValue().getRef()).isNotBlank();
        assertThat(captor.getValue().getFirstname()).isEqualTo("Jean");
        assertThat(captor.getValue().getLastname()).isEqualTo("Dupont");
        assertThat(captor.getValue().getUsername()).isEqualTo("jdupont");
        assertThat(captor.getValue().getEmail()).isEqualTo("jean@mail.com");
        assertThat(captor.getValue().getPhoneNumber()).isEqualTo("770000000");
        assertThat(captor.getValue().getRole()).isEqualTo(Roles.CLIENT);
        assertThat(captor.getValue().getEnterprise()).isEqualTo(enterprise);
    }

    @Test
    void createClient_throwsWhenEnterpriseNotFound() {
        var createDTO = mock(ClientCreateDTO.class);
        when(createDTO.enterpriseId()).thenReturn(99L);
        when(enterpriseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.createClient(createDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateClient_throwsWhenNotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.updateClient(mock(ClientUpdateDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateClient_updatesAllFieldsWhenProvided() {
        var dto = mock(ClientUpdateDTO.class);
        when(dto.firstname()).thenReturn("Jean");
        when(dto.lastname()).thenReturn("Dupont");
        when(dto.username()).thenReturn("jdupont");
        when(dto.email()).thenReturn("jean@mail.com");
        when(dto.phoneNumber()).thenReturn("770000000");
        when(dto.role()).thenReturn(Roles.CLIENT);
        when(dto.active()).thenReturn(true);

        var existing = new Client();
        var saved = new Client();
        var resultDTO = mock(ClientDTO.class);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clientRepository.saveAndFlush(existing)).thenReturn(saved);
        when(clientMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = clientService.updateClient(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getFirstname()).isEqualTo("Jean");
        assertThat(existing.getLastname()).isEqualTo("Dupont");
        assertThat(existing.getUsername()).isEqualTo("jdupont");
        assertThat(existing.getEmail()).isEqualTo("jean@mail.com");
        assertThat(existing.getPhoneNumber()).isEqualTo("770000000");
        assertThat(existing.getRole()).isEqualTo(Roles.CLIENT);
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void updateClient_keepsExistingFieldsWhenDtoFieldsNull() {
        /*Mockito's default answer for an unstubbed Boolean accessor is false, not null,
          so it needs to be stubbed explicitly to exercise the "field not provided" branch*/
        var dto = mock(ClientUpdateDTO.class);
        when(dto.active()).thenReturn(null);

        var existing = new Client();
        existing.setFirstname("Original");
        existing.setActive(true);
        var saved = new Client();
        var resultDTO = mock(ClientDTO.class);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clientRepository.saveAndFlush(existing)).thenReturn(saved);
        when(clientMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = clientService.updateClient(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getFirstname()).isEqualTo("Original");
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void deleteClient_throwsWhenNotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.deleteClient(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteClient_setsActiveToFalse() {
        var client = new Client();
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.saveAndFlush(client)).thenReturn(client);

        clientService.deleteClient(1L);

        assertThat(client.isActive()).isFalse();
        verify(clientRepository).saveAndFlush(client);
    }

    @Test
    void getClientsByFilters_withAllNullParams_returnsPage() {
        var page = new PageImpl<>(List.of(new Client()));
        when(clientRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(clientMapper.asDTO(any(Client.class))).thenReturn(mock(ClientDTO.class));

        Page<ClientDTO> result = clientService.getClientsByFilters(
                null, null, null, null, null, null, null, null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getClientsByFilters_withAllParamsSet_returnsPage() {
        var page = new PageImpl<>(List.of(new Client()));
        when(clientRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(clientMapper.asDTO(any(Client.class))).thenReturn(mock(ClientDTO.class));

        Page<ClientDTO> result = clientService.getClientsByFilters(
                1L, "REF", "John", "Doe", "jdoe", "jdoe@mail.com", "770000000",
                Roles.CLIENT, 2L, true,
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getClientById_returnsMappedDTOWhenFound() {
        var entity = new Client();
        var resultDTO = mock(ClientDTO.class);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(clientMapper.asDTO(entity)).thenReturn(resultDTO);

        var result = clientService.getClientById(1L);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void getClientById_throwsWhenNotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.getClientById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
