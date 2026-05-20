package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.EmployeeGroupDTO;
import com.sn.onepay.entity.EmployeeGroup;
import com.sn.onepay.entity.QEmployeeGroup;
import com.sn.onepay.enumeration.StateStatus;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.EmployeeGroupMapper;
import com.sn.onepay.repository.EmployeeGroupRepository;
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

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class EmployeeGroupServiceImpl implements EmployeeGroupService {

    final EmployeeGroupRepository employeeGroupRepository;
    final EmployeeGroupMapper employeeGroupMapper;

    @Override
    public EmployeeGroupDTO createEmployeeGroup(EmployeeGroupDTO employeeGroupDTO) {

        var savedGroup = employeeGroupRepository.save(employeeGroupMapper.asEntity(employeeGroupDTO));

        log.info("Created new EmployeeGroup: {}", savedGroup);
        log.trace("Created new EmployeeGroup with id: {}", savedGroup.getId());

        return employeeGroupMapper.asDTO(savedGroup);
    }

    @Override
    public EmployeeGroupDTO updateEmployeeGroup(EmployeeGroupDTO employeeGroupDTO, Long employeeGroupId) {

        EmployeeGroup existing = employeeGroupRepository.findById(employeeGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("EmployeeGroup", "ID", employeeGroupId));

        existing.setName(employeeGroupDTO.name());
        if (employeeGroupDTO.status() != null) existing.setStatus(employeeGroupDTO.status());

        var updated = employeeGroupRepository.saveAndFlush(existing);

        log.info("Updated EmployeeGroup: {}", updated);
        log.trace("Updated EmployeeGroup with id: {}", updated.getId());

        return employeeGroupMapper.asDTO(updated);
    }

    @Override
    public void deleteEmployeeGroup(Long employeeGroupId) {

        EmployeeGroup group = employeeGroupRepository.findById(employeeGroupId).orElseThrow(() -> new ResourceNotFoundException("EmployeeGroup", "ID", employeeGroupId));
        group.setStatus(StateStatus.INACTIVE);

        employeeGroupRepository.saveAndFlush(group);

        log.info("Deleted EmployeeGroup: {}", employeeGroupId);
        log.trace("Deleted EmployeeGroup with id: {}", employeeGroupId);
    }

    @Override
    public Page<EmployeeGroupDTO> getEmployeeGroupsByFilters(Long id, String ref, String name, Long enterpriseId, StateStatus status, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable) {

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
        if (status != null) {
            builder.and(employeeGroup.status.eq(status));
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
}