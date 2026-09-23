package com.sn.onepay.services.impl;

import com.sn.onepay.dto.ClientDTO;
import com.sn.onepay.dto.EnterpriseDTO;
import com.sn.onepay.entity.Client;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.ClientMapper;
import com.sn.onepay.repository.ClientRepository;
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
class ClientServiceImplTest {

    @Mock
    ClientRepository clientRepository;

    @Mock
    ClientMapper clientMapper;

    @InjectMocks
    ClientServiceImpl clientService;

    @Test
    void createClient_savesAndReturnsDTO() {
        var dto = mock(ClientDTO.class);
        var entity = new Client();
        var saved = new Client();
        var resultDTO = mock(ClientDTO.class);

        when(clientMapper.asEntity(dto)).thenReturn(entity);
        when(clientRepository.save(any(Client.class))).thenReturn(saved);
        when(clientMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = clientService.createClient(dto);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(entity.isActive()).isTrue();
        assertThat(entity.getRef()).isNotBlank();
    }

    @Test
    void updateClient_throwsWhenNotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.updateClient(mock(ClientDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateClient_updatesAllFieldsWhenProvided() {
        var dto = mock(ClientDTO.class);
        when(dto.ref()).thenReturn("REF1");
        when(dto.firstname()).thenReturn("Jean");
        when(dto.lastname()).thenReturn("Dupont");
        when(dto.username()).thenReturn("jdupont");
        when(dto.email()).thenReturn("jean@mail.com");
        when(dto.phoneNumber()).thenReturn("770000000");
        when(dto.role()).thenReturn(Roles.CLIENT);
        when(dto.enterprise()).thenReturn(mock(EnterpriseDTO.class));
        when(dto.active()).thenReturn(true);

        var existing = new Client();
        var mappedEnterprise = new Enterprise();
        var mapped = new Client();
        mapped.setEnterprise(mappedEnterprise);
        var saved = new Client();
        var resultDTO = mock(ClientDTO.class);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clientMapper.asEntity(dto)).thenReturn(mapped);
        when(clientRepository.saveAndFlush(existing)).thenReturn(saved);
        when(clientMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = clientService.updateClient(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getRef()).isEqualTo("REF1");
        assertThat(existing.getFirstname()).isEqualTo("Jean");
        assertThat(existing.getLastname()).isEqualTo("Dupont");
        assertThat(existing.getUsername()).isEqualTo("jdupont");
        assertThat(existing.getEmail()).isEqualTo("jean@mail.com");
        assertThat(existing.getPhoneNumber()).isEqualTo("770000000");
        assertThat(existing.getRole()).isEqualTo(Roles.CLIENT);
        assertThat(existing.getEnterprise()).isEqualTo(mappedEnterprise);
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void updateClient_keepsExistingFieldsWhenDtoFieldsNull() {
        /*Mockito's default answer for an unstubbed Boolean accessor is false, not null,
          so it needs to be stubbed explicitly to exercise the "field not provided" branch*/
        var dto = mock(ClientDTO.class);
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
        verify(clientMapper, never()).asEntity(any());
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