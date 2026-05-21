package com.sn.onepay.services.impl;

import com.sn.onepay.dto.ClientDTO;
import com.sn.onepay.entity.Client;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.enumeration.StateStatus;
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
        when(clientRepository.save(entity)).thenReturn(saved);
        when(clientMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = clientService.createClient(dto);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void updateClient_throwsWhenNotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.updateClient(mock(ClientDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateClient_savesWhenFound() {
        var dto = mock(ClientDTO.class);
        var existing = new Client();
        var updated = new Client();
        var resultDTO = mock(ClientDTO.class);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clientMapper.asEntity(dto)).thenReturn(updated);
        when(clientRepository.saveAndFlush(updated)).thenReturn(updated);
        when(clientMapper.asDTO(updated)).thenReturn(resultDTO);

        var result = clientService.updateClient(dto, 1L);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void deleteClient_throwsWhenNotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.deleteClient(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteClient_deletesWhenFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(new Client()));
        doNothing().when(clientRepository).deleteById(1L);

        clientService.deleteClient(1L);

        verify(clientRepository).deleteById(1L);
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
                Roles.CLIENT, 2L, StateStatus.ACTIVE,
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }
}