package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.SubventionDTO;
import com.sn.onepay.entity.QSubvention;
import com.sn.onepay.entity.Subvention;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.SubventionMapper;
import com.sn.onepay.repository.SubventionRepository;
import com.sn.onepay.services.SubventionService;
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
public class SubventionServiceImpl implements SubventionService {

    final SubventionRepository subventionRepository;
    final SubventionMapper subventionMapper;

    @Override
    public SubventionDTO createSubvention(SubventionDTO subventionDTO) {

        if (Math.abs(subventionDTO.employeePercent() + subventionDTO.employerPercent() - 100.0) > 0.001) {
            throw new ObjectValidationException("La somme des pourcentages employé et employeur doit être égale à 100");
        }

        if (subventionDTO.partnership() == null || !Boolean.TRUE.equals(subventionDTO.partnership().active())) {
            throw new ObjectValidationException("Un partenariat actif est requis pour créer une subvention");
        }

        Subvention subvention = subventionMapper.asEntity(subventionDTO);
        subvention.setRef(UUID.randomUUID().toString());
        subvention.setActive(true);
        var savedSubvention = subventionRepository.save(subvention);

        log.info("Created new Subvention: {}", savedSubvention);
        log.trace("Created new Subvention with id: {}", savedSubvention.getId());

        return subventionMapper.asDTO(savedSubvention);
    }

    @Override
    public SubventionDTO updateSubvention(SubventionDTO subventionDTO, Long subventionId) {

        Subvention existing = subventionRepository.findById(subventionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subvention", "ID", subventionId));

        if (subventionDTO.employeePercent() != null) existing.setEmployeePercent(subventionDTO.employeePercent());
        if (subventionDTO.employerPercent() != null) existing.setEmployerPercent(subventionDTO.employerPercent());
        if (subventionDTO.active() != null) existing.setActive(subventionDTO.active());

        var updated = subventionRepository.saveAndFlush(existing);

        log.info("Updated Subvention: {}", updated);
        log.trace("Updated Subvention with id: {}", updated.getId());

        return subventionMapper.asDTO(updated);
    }

    @Override
    public void deleteSubvention(Long subventionId) {

        Subvention subvention = subventionRepository.findById(subventionId).orElseThrow(() -> new ResourceNotFoundException("Subvention", "ID", subventionId));
        subvention.setActive(false);
        subventionRepository.saveAndFlush(subvention);

        log.info("Deleted Subvention: {}", subventionId);
        log.trace("Deleted Subvention with id: {}", subventionId);
    }

    @Override
    public Page<SubventionDTO> getSubventionsByFilters(Long id, String ref, Double employeePercent, Double employerPercent, Long partnershipId, Long employeeGroupId, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable) {

        QSubvention subvention = QSubvention.subvention;
        BooleanBuilder builder = new BooleanBuilder();

        if (id != null) {
            builder.and(subvention.id.eq(id));
        }
        if (ref != null && !ref.isEmpty()) {
            builder.and(subvention.ref.containsIgnoreCase(ref));
        }
        if (employeePercent != null) {
            builder.and(subvention.employeePercent.eq(employeePercent));
        }
        if (employerPercent != null) {
            builder.and(subvention.employerPercent.eq(employerPercent));
        }
        if (partnershipId != null) {
            builder.and(subvention.partnership.id.eq(partnershipId));
        }
        if (employeeGroupId != null) {
            builder.and(subvention.employeeGroup.id.eq(employeeGroupId));
        }
        if (active != null) {
            builder.and(subvention.active.eq(active));
        }
        if (creationDate != null) {
            builder.and(subvention.creationDate.goe(creationDate));
        }
        if (modificationDate != null) {
            builder.and(subvention.modificationDate.loe(modificationDate));
        }

        Page<Subvention> result = subventionRepository.findAll(builder, pageable);
        return result.map(subventionMapper::asDTO);
    }
}