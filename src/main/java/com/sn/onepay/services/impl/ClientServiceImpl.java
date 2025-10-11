package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.ClientDTO;
import com.sn.onepay.entity.Client;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.entity.QClient;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.enumeration.StateStatus;
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

import java.time.LocalDateTime;

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
    public Page<ClientDTO> getClientsByFilters(Long id, String ref, String firstname, String lastname, String username, String email, String phoneNumber, Roles role, Enterprise enterprise, StateStatus status, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable) {

        QClient client = QClient.client;
        BooleanBuilder builder = new BooleanBuilder();

        if (id != null) {
            builder.and(client.id.eq(id));
        }
        if (ref != null && !ref.isEmpty()) {
            builder.and(client.ref.containsIgnoreCase(ref));
        }
        if (firstname != null && !firstname.isEmpty()) {
            builder.and(client.firstname.containsIgnoreCase(firstname));
        }
        if (lastname != null && !lastname.isEmpty()) {
            builder.and(client.lastname.containsIgnoreCase(lastname));
        }
        if (username != null && !username.isEmpty()) {
            builder.and(client.username.containsIgnoreCase(username));
        }
        if (email != null && !email.isEmpty()) {
            builder.and(client.email.containsIgnoreCase(email));
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            builder.and(client.phoneNumber.containsIgnoreCase(phoneNumber));
        }
        if (role != null) {
            builder.and(client.role.eq(role));
        }
        if (enterprise != null) {
            builder.and(client.enterprise.eq(enterprise));
        }
        if (status != null) {
            builder.and(client.status.eq(status));
        }
        if (creationDate != null) {
            builder.and(client.creationDate.goe(creationDate));
        }
        if (modificationDate != null) {
            builder.and(client.modificationDate.loe(modificationDate));
        }

        Page<Client> result = clientRepository.findAll(builder, pageable);

        return result.map(clientMapper::asDTO);
    }
}
