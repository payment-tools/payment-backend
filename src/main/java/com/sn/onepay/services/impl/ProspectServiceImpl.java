package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.ProspectCreateDTO;
import com.sn.onepay.dto.ProspectDTO;
import com.sn.onepay.dto.ProspectUpdateDTO;
import com.sn.onepay.entity.Prospect;
import com.sn.onepay.entity.QProspect;
import com.sn.onepay.enumeration.ProspectScore;
import com.sn.onepay.enumeration.ProspectStage;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.ProspectMapper;
import com.sn.onepay.repository.ProspectRepository;
import com.sn.onepay.services.ProspectService;
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
public class ProspectServiceImpl implements ProspectService {

    final ProspectRepository prospectRepository;
    final ProspectMapper prospectMapper;

    @Override
    public ProspectDTO createProspect(ProspectCreateDTO prospectCreateDTO) {

        Prospect prospect = new Prospect();
        prospect.setRef(UUID.randomUUID().toString());
        prospect.setCompanyName(prospectCreateDTO.companyName());
        prospect.setContactName(prospectCreateDTO.contactName());
        prospect.setEmail(prospectCreateDTO.email());
        prospect.setPhoneNumber(prospectCreateDTO.phoneNumber());
        prospect.setStage(prospectCreateDTO.stage() != null ? prospectCreateDTO.stage() : ProspectStage.DECOUVERTE);
        prospect.setNote(prospectCreateDTO.note());
        prospect.setPotentialEmployees(prospectCreateDTO.potentialEmployees());
        prospect.setAssignedTo(prospectCreateDTO.assignedTo());
        prospect.setScore(prospectCreateDTO.score() != null ? prospectCreateDTO.score() : ProspectScore.COLD);
        prospect.setActive(true);

        var savedProspect = prospectRepository.save(prospect);

        log.info("Created new Prospect: {}", savedProspect);
        log.trace("Created new Prospect with id: {}", savedProspect.getId());

        return prospectMapper.asDTO(savedProspect);
    }

    @Override
    public ProspectDTO updateProspect(ProspectUpdateDTO prospectUpdateDTO, Long prospectId) {

        Prospect existing = prospectRepository.findById(prospectId)
                .orElseThrow(() -> new ResourceNotFoundException("Prospect", "ID", prospectId));

        if (prospectUpdateDTO.companyName() != null) existing.setCompanyName(prospectUpdateDTO.companyName());
        if (prospectUpdateDTO.contactName() != null) existing.setContactName(prospectUpdateDTO.contactName());
        if (prospectUpdateDTO.email() != null) existing.setEmail(prospectUpdateDTO.email());
        if (prospectUpdateDTO.phoneNumber() != null) existing.setPhoneNumber(prospectUpdateDTO.phoneNumber());
        if (prospectUpdateDTO.stage() != null) existing.setStage(prospectUpdateDTO.stage());
        if (prospectUpdateDTO.note() != null) existing.setNote(prospectUpdateDTO.note());
        if (prospectUpdateDTO.potentialEmployees() != null) existing.setPotentialEmployees(prospectUpdateDTO.potentialEmployees());
        if (prospectUpdateDTO.assignedTo() != null) existing.setAssignedTo(prospectUpdateDTO.assignedTo());
        if (prospectUpdateDTO.score() != null) existing.setScore(prospectUpdateDTO.score());
        if (prospectUpdateDTO.active() != null) existing.setActive(prospectUpdateDTO.active());

        var updatedProspect = prospectRepository.saveAndFlush(existing);

        log.info("Updated Prospect: {}", updatedProspect);
        log.trace("Updated Prospect with id: {}", updatedProspect.getId());

        return prospectMapper.asDTO(updatedProspect);
    }

    @Override
    public void deleteProspect(Long prospectId) {

        Prospect prospect = prospectRepository.findById(prospectId)
                .orElseThrow(() -> new ResourceNotFoundException("Prospect", "ID", prospectId));

        /*Implements logical deletion*/
        prospect.setActive(false);
        prospectRepository.saveAndFlush(prospect);

        log.info("Deleted Prospect with id: {}", prospectId);
        log.trace("Deleted Prospect with id: {}", prospectId);
    }

    @Override
    public Page<ProspectDTO> getProspectsByFilters(Long id, String ref, String companyName, ProspectStage stage, ProspectScore score, String assignedTo, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable) {

        QProspect prospect = QProspect.prospect;
        BooleanBuilder builder = new BooleanBuilder();

        if (id != null) {
            builder.and(prospect.id.eq(id));
        }
        if (ref != null && !ref.isEmpty()) {
            builder.and(prospect.ref.containsIgnoreCase(ref));
        }
        if (companyName != null && !companyName.isEmpty()) {
            builder.and(prospect.companyName.containsIgnoreCase(companyName));
        }
        if (stage != null) {
            builder.and(prospect.stage.eq(stage));
        }
        if (score != null) {
            builder.and(prospect.score.eq(score));
        }
        if (assignedTo != null && !assignedTo.isEmpty()) {
            builder.and(prospect.assignedTo.containsIgnoreCase(assignedTo));
        }
        if (active != null) {
            builder.and(prospect.active.eq(active));
        }
        if (creationDate != null) {
            builder.and(prospect.creationDate.goe(creationDate));
        }
        if (modificationDate != null) {
            builder.and(prospect.modificationDate.loe(modificationDate));
        }

        Page<Prospect> result = prospectRepository.findAll(builder, pageable);

        return result.map(prospectMapper::asDTO);
    }

    @Override
    public ProspectDTO getProspectById(Long id) {
        return prospectMapper.asDTO(prospectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Prospect", "ID", id)));
    }
}
