package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.SubventionCreateDTO;
import com.sn.onepay.dto.SubventionDTO;
import com.sn.onepay.dto.SubventionUpdateDTO;
import com.sn.onepay.entity.EmployeeGroup;
import com.sn.onepay.entity.Partnership;
import com.sn.onepay.entity.QSubvention;
import com.sn.onepay.entity.Subvention;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.SubventionMapper;
import com.sn.onepay.repository.EmployeeGroupRepository;
import com.sn.onepay.repository.PartnershipRepository;
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
    final PartnershipRepository partnershipRepository;
    final EmployeeGroupRepository employeeGroupRepository;
    final SubventionMapper subventionMapper;

    @Override
    public SubventionDTO createSubvention(SubventionCreateDTO subventionCreateDTO) {

        validatePercentages(subventionCreateDTO.employeePercent(), subventionCreateDTO.employerPercent());

        Partnership partnership = partnershipRepository.findById(subventionCreateDTO.partnershipId())
                .orElseThrow(() -> new ResourceNotFoundException("Partnership", "ID", subventionCreateDTO.partnershipId()));

        if (!partnership.isActive()) {
            throw new ObjectValidationException("Un partenariat actif est requis pour créer une subvention");
        }

        EmployeeGroup employeeGroup = employeeGroupRepository.findById(subventionCreateDTO.employeeGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("EmployeeGroup", "ID", subventionCreateDTO.employeeGroupId()));

        if (!employeeGroup.isActive()) {
            throw new ObjectValidationException("Un groupe de collaborateurs actif est requis pour créer une subvention");
        }

        if (employeeGroup.getEnterprise() == null || partnership.getEnterprise() == null
                || !employeeGroup.getEnterprise().getId().equals(partnership.getEnterprise().getId())) {
            throw new ObjectValidationException("Le groupe de collaborateurs doit appartenir à l'entreprise du partenariat");
        }

        Subvention subvention = new Subvention();
        subvention.setRef(UUID.randomUUID().toString());
        subvention.setEmployeePercent(subventionCreateDTO.employeePercent());
        subvention.setEmployerPercent(subventionCreateDTO.employerPercent());
        subvention.setPartnership(partnership);
        subvention.setEmployeeGroup(employeeGroup);
        subvention.setActive(true);
        var savedSubvention = subventionRepository.save(subvention);

        log.info("Created new Subvention: {}", savedSubvention);
        log.trace("Created new Subvention with id: {}", savedSubvention.getId());

        return subventionMapper.asDTO(savedSubvention);
    }

    @Override
    public SubventionDTO updateSubvention(SubventionUpdateDTO subventionUpdateDTO, Long subventionId) {

        Subvention existing = subventionRepository.findById(subventionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subvention", "ID", subventionId));

        if (subventionUpdateDTO.employeePercent() != null || subventionUpdateDTO.employerPercent() != null) {
            validatePercentages(subventionUpdateDTO.employeePercent(), subventionUpdateDTO.employerPercent());
            existing.setEmployeePercent(subventionUpdateDTO.employeePercent());
            existing.setEmployerPercent(subventionUpdateDTO.employerPercent());
        }
        if (subventionUpdateDTO.active() != null) existing.setActive(subventionUpdateDTO.active());

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

    private void validatePercentages(Double employeePercent, Double employerPercent) {

        if (employeePercent == null || employerPercent == null) {
            throw new ObjectValidationException("Les pourcentages employé et employeur doivent être fournis ensemble");
        }
        if (Math.abs(employeePercent + employerPercent - 100.0) > 0.001) {
            throw new ObjectValidationException("La somme des pourcentages employé et employeur doit être égale à 100");
        }
    }
}
