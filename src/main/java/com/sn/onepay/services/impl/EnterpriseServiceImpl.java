package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.EnterpriseDTO;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.entity.QEnterprise;
import com.sn.onepay.enumeration.Modules;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.EnterpriseMapper;
import com.sn.onepay.repository.EnterpriseRepository;
import com.sn.onepay.services.EnterpriseService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class EnterpriseServiceImpl implements EnterpriseService {

    final EnterpriseRepository enterpriseRepository;
    final EnterpriseMapper enterpriseMapper;

    @Override
    public EnterpriseDTO createEnterprise(EnterpriseDTO enterpriseDTO) {

        Enterprise enterprise = enterpriseMapper.asEntity(enterpriseDTO);
        enterprise.setActualQuota(0L);
        enterprise.setActive(true);

        var savedEnterprise = enterpriseRepository.save(enterprise);

        log.info("Created new Enterprise: {}", savedEnterprise);
        log.trace("Created new Enterprise with id: {}", savedEnterprise.getId());

        return enterpriseMapper.asDTO(savedEnterprise);
    }

    @Override
    public EnterpriseDTO updateEnterprise(EnterpriseDTO enterpriseDTO, Long enterpriseId) {

        Enterprise existing = enterpriseRepository.findById(enterpriseId).orElseThrow(() -> new ResourceNotFoundException("Enterprise", "ID", enterpriseId));

        if (enterpriseDTO.ref() != null) existing.setRef(enterpriseDTO.ref());
        if (enterpriseDTO.name() != null) existing.setName(enterpriseDTO.name());
        if (enterpriseDTO.maxQuota() != null) existing.setMaxQuota(enterpriseDTO.maxQuota());
        if (enterpriseDTO.actualQuota() != null) existing.setActualQuota(enterpriseDTO.actualQuota());
        if (enterpriseDTO.address() != null) existing.setAddress(enterpriseDTO.address());
        if (enterpriseDTO.enrolledModules() != null) existing.setEnrolledModules(enterpriseDTO.enrolledModules());
        if (enterpriseDTO.active() != null) existing.setActive(enterpriseDTO.active());

        var updatedEnterprise = enterpriseRepository.saveAndFlush(existing);

        log.info("Updated Enterprise: {}", updatedEnterprise);
        log.trace("Updated Enterprise with id: {}", updatedEnterprise.getId());

        return enterpriseMapper.asDTO(updatedEnterprise);

    }

    @Override
    public void deleteEnterprise(Long enterpriseId) {

        Enterprise enterprise = enterpriseRepository.findById(enterpriseId).orElseThrow(() -> new ResourceNotFoundException("Enterprise", "ID", enterpriseId));

        /*Implements logical deletion*/
        enterprise.setActive(false);
        enterpriseRepository.saveAndFlush(enterprise);

        log.info("Deleted Enterprise: {}", enterpriseId);
        log.trace("Deleted Enterprise with id: {}", enterpriseId);

    }

    @Override
    public Page<EnterpriseDTO> getEnterprisesByFilters(Long id, String ref, String name, Long maxQuota, Long actualQuota, String address, Modules enrolledModules, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable) {

        QEnterprise enterprise = QEnterprise.enterprise;
        BooleanBuilder builder = new BooleanBuilder();

        if (id != null) {
            builder.and(enterprise.id.eq(id));
        }
        if (ref != null && !ref.isEmpty()) {
            builder.and(enterprise.ref.containsIgnoreCase(ref));
        }
        if (name != null && !name.isEmpty()) {
            builder.and(enterprise.name.containsIgnoreCase(name));
        }
        if (maxQuota != null) {
            builder.and(enterprise.maxQuota.eq(maxQuota));
        }
        if (actualQuota != null) {
            builder.and(enterprise.actualQuota.eq(actualQuota));
        }
        if (address != null && !address.isEmpty()) {
            builder.and(enterprise.address.containsIgnoreCase(address));
        }
        if (enrolledModules != null) {
            // On vérifie si le module de l'entreprise est dans la collection
            builder.and(enterprise.enrolledModules.contains(enrolledModules));
        }
        if (active != null) {
            builder.and(enterprise.active.eq(active));
        }
        if (creationDate != null) {
            builder.and(enterprise.creationDate.goe(creationDate));
        }
        if (modificationDate != null) {
            builder.and(enterprise.modificationDate.loe(modificationDate));
        }

        Page<Enterprise> result = enterpriseRepository.findAll(builder, pageable);
        return result.map(enterpriseMapper::asDTO);
    }
}
