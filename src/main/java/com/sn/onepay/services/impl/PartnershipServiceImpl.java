package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.PartnershipDTO;
import com.sn.onepay.entity.Partnership;
import com.sn.onepay.entity.QPartnership;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceAlreadyExistException;
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

import java.time.LocalDateTime;
import java.util.Objects;

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

        /*Check if partnership already exist*/
        if(Objects.nonNull(partnershipRepository.findPartnershipBySalesIdAndEnterpriseId(partnershipDTO.sales().id(), partnershipDTO.enterprise().id())))
            throw new ResourceAlreadyExistException("Partnership", partnershipDTO);

        /*Check if partnership is allowed*/
        if(partnershipDTO.enterprise().enrolledModules().contains(partnershipDTO.sales().type())){

            Partnership partnership = partnershipMapper.asEntity(partnershipDTO);
            partnership.setActive(true);
            var savedPartnership = partnershipRepository.save(partnership);

            log.info("Created Partnership: {}", savedPartnership);
            log.trace("Created Partnership with id: {}", savedPartnership.getId());

            return partnershipMapper.asDTO(savedPartnership);
        }
        else
            throw new ObjectValidationException("Création de partenariat non autorisée");

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

        Partnership partnership = partnershipRepository.findById(partnershipId).orElseThrow( () -> new ResourceNotFoundException("Partnership", "ID", partnershipId));
        partnership.setActive(false);
        partnershipRepository.saveAndFlush(partnership);

        log.info("Deleted Partnership with id: {}", partnershipId);
        log.trace("Deleted Partnership with id: {}", partnershipId);

    }

    @Override
    public Page<PartnershipDTO> getPartnershipsByFilters(Long id, String ref, Long salesId, Long enterpriseId, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable) {

        QPartnership partnership = QPartnership.partnership;
        BooleanBuilder builder = new BooleanBuilder();

        if (id != null) {
            builder.and(partnership.id.eq(id));
        }
        if (ref != null && !ref.isEmpty()) {
            builder.and(partnership.ref.containsIgnoreCase(ref));
        }
        if (salesId != null) {
            builder.and(partnership.sales.id.eq(salesId));
        }
        if (enterpriseId != null) {
            builder.and(partnership.enterprise.id.eq(enterpriseId));
        }
        if (active != null) {
            builder.and(partnership.active.eq(active));
        }
        if (creationDate != null) {
            builder.and(partnership.creationDate.goe(creationDate));
        }
        if (modificationDate != null) {
            builder.and(partnership.modificationDate.loe(modificationDate));
        }

        Page<Partnership> result = partnershipRepository.findAll(builder, pageable);

        return result.map(partnershipMapper::asDTO);
    }

    @Override
    public PartnershipDTO getPartnershipsBySalesIdAndEnterpriseId(Long salesId, Long enterpriseId) {
            return partnershipMapper.asDTO(partnershipRepository.findPartnershipBySalesIdAndEnterpriseId(salesId, enterpriseId));
    }
}