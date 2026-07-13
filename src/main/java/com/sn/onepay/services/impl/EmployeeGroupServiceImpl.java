package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.EmployeeGroupCreateDTO;
import com.sn.onepay.dto.EmployeeGroupDTO;
import com.sn.onepay.dto.EmployeeGroupUpdateDTO;
import com.sn.onepay.entity.Client;
import com.sn.onepay.entity.EmployeeGroup;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.entity.QEmployeeGroup;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.EmployeeGroupMapper;
import com.sn.onepay.repository.ClientRepository;
import com.sn.onepay.repository.EmployeeGroupRepository;
import com.sn.onepay.repository.EnterpriseRepository;
import com.sn.onepay.services.EmployeeGroupService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class EmployeeGroupServiceImpl implements EmployeeGroupService {

    final EmployeeGroupRepository employeeGroupRepository;
    final EnterpriseRepository enterpriseRepository;
    final ClientRepository clientRepository;
    final EmployeeGroupMapper employeeGroupMapper;

    @Override
    public EmployeeGroupDTO createEmployeeGroup(EmployeeGroupCreateDTO employeeGroupCreateDTO) {

        Enterprise enterprise = enterpriseRepository.findById(employeeGroupCreateDTO.enterpriseId())
                .orElseThrow(() -> new ResourceNotFoundException("Enterprise", "ID", employeeGroupCreateDTO.enterpriseId()));

        EmployeeGroup group = new EmployeeGroup();
        group.setRef(UUID.randomUUID().toString());
        group.setName(employeeGroupCreateDTO.name());
        group.setEnterprise(enterprise);
        if (employeeGroupCreateDTO.clientIds() != null) {
            group.setClients(resolveClients(employeeGroupCreateDTO.clientIds(), enterprise));
        }
        group.setActive(true);
        var savedGroup = employeeGroupRepository.save(group);

        log.info("Created new EmployeeGroup: {}", savedGroup);
        log.trace("Created new EmployeeGroup with id: {}", savedGroup.getId());

        return employeeGroupMapper.asDTO(savedGroup);
    }

    @Override
    public EmployeeGroupDTO updateEmployeeGroup(EmployeeGroupUpdateDTO employeeGroupUpdateDTO, Long employeeGroupId) {

        EmployeeGroup existing = employeeGroupRepository.findById(employeeGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("EmployeeGroup", "ID", employeeGroupId));

        if (employeeGroupUpdateDTO.name() != null) existing.setName(employeeGroupUpdateDTO.name());
        if (employeeGroupUpdateDTO.clientIds() != null) {
            existing.setClients(resolveClients(employeeGroupUpdateDTO.clientIds(), existing.getEnterprise()));
        }
        if (employeeGroupUpdateDTO.active() != null) existing.setActive(employeeGroupUpdateDTO.active());

        var updated = employeeGroupRepository.saveAndFlush(existing);

        log.info("Updated EmployeeGroup: {}", updated);
        log.trace("Updated EmployeeGroup with id: {}", updated.getId());

        return employeeGroupMapper.asDTO(updated);
    }

    @Override
    public void deleteEmployeeGroup(Long employeeGroupId) {

        EmployeeGroup group = employeeGroupRepository.findById(employeeGroupId).orElseThrow(() -> new ResourceNotFoundException("EmployeeGroup", "ID", employeeGroupId));
        group.setActive(false);
        employeeGroupRepository.saveAndFlush(group);

        log.info("Deleted EmployeeGroup: {}", employeeGroupId);
        log.trace("Deleted EmployeeGroup with id: {}", employeeGroupId);
    }

    @Override
    public Page<EmployeeGroupDTO> getEmployeeGroupsByFilters(Long id, String ref, String name, Long enterpriseId, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable) {

        QEmployeeGroup employeeGroup = QEmployeeGroup.employeeGroup;
        BooleanBuilder builder = new BooleanBuilder();

        if (id != null) {
            builder.and(employeeGroup.id.eq(id));
        }
        if (ref != null && !ref.isEmpty()) {
            builder.and(employeeGroup.ref.containsIgnoreCase(ref));
        }
        if (name != null && !name.isEmpty()) {
            builder.and(employeeGroup.name.containsIgnoreCase(name));
        }
        if (enterpriseId != null) {
            builder.and(employeeGroup.enterprise.id.eq(enterpriseId));
        }
        if (active != null) {
            builder.and(employeeGroup.active.eq(active));
        }
        if (creationDate != null) {
            builder.and(employeeGroup.creationDate.goe(creationDate));
        }
        if (modificationDate != null) {
            builder.and(employeeGroup.modificationDate.loe(modificationDate));
        }

        Page<EmployeeGroup> result = employeeGroupRepository.findAll(builder, pageable);
        return result.map(employeeGroupMapper::asDTO);
    }

    private List<Client> resolveClients(List<Long> clientIds, Enterprise enterprise) {

        List<Client> clients = new ArrayList<>();
        for (Long clientId : clientIds) {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Client", "ID", clientId));
            if (!client.isActive()) {
                throw new ObjectValidationException("Le client " + clientId + " est inactif");
            }
            if (client.getEnterprise() == null || enterprise == null
                    || !client.getEnterprise().getId().equals(enterprise.getId())) {
                throw new ObjectValidationException("Le client " + clientId + " n'appartient pas à l'entreprise du groupe");
            }
            clients.add(client);
        }
        return clients;
    }
}
