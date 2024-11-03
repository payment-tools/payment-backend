package com.sn.onepay.services.impl;

import com.sn.onepay.dto.ClientDTO;
import com.sn.onepay.exceptions.ResourceAlreadyExistException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.ClientMapper;
import com.sn.onepay.repository.ClientRepository;
import com.sn.onepay.services.ClientService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    final ClientRepository clientRepository;
    final ClientMapper clientMapper;

    @Override
    public ClientDTO createClient(ClientDTO clientDTO) {

        var savedClient = clientRepository.save(clientMapper.asEntity(clientDTO));

        log.info("Created new client: {}", savedClient);
        log.trace("Created new client with id: {}", savedClient.getId());

        return clientMapper.asDTO(savedClient);

    }

    @Override
    public ClientDTO updateClient(ClientDTO clientDTO, Long clientId) {

        clientRepository.findById(clientId).orElseThrow( () -> new ResourceNotFoundException("Client", "ID", clientId));

        var updatedClient = clientRepository.saveAndFlush(clientMapper.asEntity(clientDTO));

        log.info("Updated client: {}", updatedClient);
        log.trace("Updated client with id: {}", updatedClient.getId());

        return clientMapper.asDTO(updatedClient);
    }

    @Override
    public void deleteClient(Long clientId) {

        clientRepository.findById(clientId).orElseThrow( () -> new ResourceNotFoundException("Client", "ID", clientId));

        clientRepository.deleteById(clientId);

        log.info("Deleted client: {}", clientId);
        log.trace("Deleted client with id: {}", clientId);

    }

    @Override
    public Page<ClientDTO> getClientsByFilter(ClientDTO clientDTO, Pageable pageable) {
        return null;
    }
}
