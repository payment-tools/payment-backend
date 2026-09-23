package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.ClientCreateDTO;
import com.sn.onepay.dto.ClientDTO;
import com.sn.onepay.dto.ClientUpdateDTO;
import com.sn.onepay.entity.Client;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.entity.QClient;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.ClientMapper;
import com.sn.onepay.repository.ClientRepository;
import com.sn.onepay.repository.EnterpriseRepository;
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
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    final ClientRepository clientRepository;
    final EnterpriseRepository enterpriseRepository;
    final ClientMapper clientMapper;

    @Override
    public ClientDTO createClient(ClientCreateDTO clientCreateDTO) {

        Enterprise enterprise = enterpriseRepository.findById(clientCreateDTO.enterpriseId())
                .orElseThrow(() -> new ResourceNotFoundException("Enterprise", "ID", clientCreateDTO.enterpriseId()));

        Client client = new Client();
        client.setRef(UUID.randomUUID().toString());
        client.setFirstname(clientCreateDTO.firstname());
        client.setLastname(clientCreateDTO.lastname());
        client.setUsername(clientCreateDTO.username());
        client.setEmail(clientCreateDTO.email());
        client.setPhoneNumber(clientCreateDTO.phoneNumber());
        client.setRole(clientCreateDTO.role());
        client.setEnterprise(enterprise);
        client.setActive(true);
        var savedClient = clientRepository.save(client);

        log.info("Created new client: {}", savedClient);
        log.trace("Created new client with id: {}", savedClient.getId());

        return clientMapper.asDTO(savedClient);

    }

    @Override
    public ClientDTO updateClient(ClientUpdateDTO clientUpdateDTO, Long clientId) {

        Client existing = clientRepository.findById(clientId).orElseThrow( () -> new ResourceNotFoundException("Client", "ID", clientId));

        if (clientUpdateDTO.firstname() != null) existing.setFirstname(clientUpdateDTO.firstname());
        if (clientUpdateDTO.lastname() != null) existing.setLastname(clientUpdateDTO.lastname());
        if (clientUpdateDTO.username() != null) existing.setUsername(clientUpdateDTO.username());
        if (clientUpdateDTO.email() != null) existing.setEmail(clientUpdateDTO.email());
        if (clientUpdateDTO.phoneNumber() != null) existing.setPhoneNumber(clientUpdateDTO.phoneNumber());
        if (clientUpdateDTO.role() != null) existing.setRole(clientUpdateDTO.role());
        if (clientUpdateDTO.active() != null) existing.setActive(clientUpdateDTO.active());

        var updatedClient = clientRepository.saveAndFlush(existing);

        log.info("Updated client: {}", updatedClient);
        log.trace("Updated client with id: {}", updatedClient.getId());

        return clientMapper.asDTO(updatedClient);
    }

    @Override
    public void deleteClient(Long clientId) {

        Client client = clientRepository.findById(clientId).orElseThrow( () -> new ResourceNotFoundException("Client", "ID", clientId));
        client.setActive(false);
        clientRepository.saveAndFlush(client);

        log.info("Deleted client: {}", clientId);
        log.trace("Deleted client with id: {}", clientId);

    }

    @Override
    public Page<ClientDTO> getClientsByFilters(Long id, String ref, String firstname, String lastname, String username, String email, String phoneNumber, Roles role, Long enterpriseId, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable) {

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
        if (enterpriseId != null) {
            builder.and(client.enterprise.id.eq(enterpriseId));
        }
        if (active != null) {
            builder.and(client.active.eq(active));
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

    @Override
    public ClientDTO getClientById(Long id) {
        return clientMapper.asDTO(clientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Client", "ID", id)));
    }
}