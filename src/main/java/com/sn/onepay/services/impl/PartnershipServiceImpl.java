package com.sn.onepay.services.impl;

import com.sn.onepay.dto.PartnershipDTO;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.PartnershipMapper;
import com.sn.onepay.repository.PartnershipRepository;
import com.sn.onepay.services.PartnershipService;
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
public class PartnershipServiceImpl implements PartnershipService {

    final PartnershipRepository partnershipRepository;
    final PartnershipMapper partnershipMapper;


    @Override
    public PartnershipDTO createPartnership(PartnershipDTO partnershipDTO) {

        var savedPartnership = partnershipRepository.save(partnershipMapper.asEntity(partnershipDTO));

        log.info("Created Partnership: {}", savedPartnership);
        log.trace("Created Partnership with id: {}", savedPartnership.getId());

        return partnershipMapper.asDTO(savedPartnership);
    }

    @Override
    public PartnershipDTO updatePartnership(PartnershipDTO partnershipDTO, Long partnershipId) {

        partnershipRepository.findById(partnershipId).orElseThrow( () -> new ResourceNotFoundException("Partnership", "ID", partnershipId));

        var updatedPartnership = partnershipRepository.saveAndFlush(partnershipMapper.asEntity(partnershipDTO));

        log.info("Updated Partnership: {}", updatedPartnership);
        log.trace("Updated Partnership with id: {}", updatedPartnership.getId());

        return partnershipMapper.asDTO(updatedPartnership);
    }

    @Override
    public void deletePartnership(Long partnershipId) {

        partnershipRepository.findById(partnershipId).orElseThrow( () -> new ResourceNotFoundException("Partnership", "ID", partnershipId));

        partnershipRepository.deleteById(partnershipId);

        log.info("Deleted Partnership with id: {}", partnershipId);
        log.trace("Deleted Partnership with id: {}", partnershipId);

    }

    @Override
    public Page<PartnershipDTO> getPartnershipsByFilter(PartnershipDTO partnershipDTO, Pageable pageable) {
        return null;
    }
}
