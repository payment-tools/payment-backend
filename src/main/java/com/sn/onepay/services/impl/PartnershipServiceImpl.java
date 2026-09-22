package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.PartnershipDTO;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.entity.Partnership;
import com.sn.onepay.entity.QPartnership;
import com.sn.onepay.entity.Sales;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceAlreadyExistException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.PartnershipMapper;
import com.sn.onepay.repository.EnterpriseRepository;
import com.sn.onepay.repository.PartnershipRepository;
import com.sn.onepay.repository.SalesRepository;
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
    final EnterpriseRepository enterpriseRepository;
    final SalesRepository salesRepository;


    @Override
    public PartnershipDTO createPartnership(PartnershipDTO partnershipDTO) {

        /*Reload the real entities by id instead of trusting the business fields the client sent*/
        Enterprise enterprise = enterpriseRepository.findById(partnershipDTO.enterprise().id())
                .orElseThrow(() -> new ResourceNotFoundException("Enterprise", "ID", partnershipDTO.enterprise().id()));
        Sales sales = salesRepository.findById(partnershipDTO.sales().id())
                .orElseThrow(() -> new ResourceNotFoundException("Sales", "ID", partnershipDTO.sales().id()));

        /*Check if partnership already exist*/
        if(Objects.nonNull(partnershipRepository.findPartnershipBySalesIdAndEnterpriseId(sales.getId(), enterprise.getId())))
            throw new ResourceAlreadyExistException("Partnership", partnershipDTO);

        /*Check if partnership is allowed*/
        if(enterprise.getEnrolledModules() != null && enterprise.getEnrolledModules().contains(sales.getType())){

            Partnership partnership = new Partnership();
            partnership.setRef(partnershipDTO.ref());
            partnership.setEnterprise(enterprise);
            partnership.setSales(sales);
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

        Partnership existing = partnershipRepository.findById(partnershipId).orElseThrow( () -> new ResourceNotFoundException("Partnership", "ID", partnershipId));

        if (partnershipDTO.ref() != null) existing.setRef(partnershipDTO.ref());
        if (partnershipDTO.sales() != null)
            existing.setSales(salesRepository.findById(partnershipDTO.sales().id())
                    .orElseThrow(() -> new ResourceNotFoundException("Sales", "ID", partnershipDTO.sales().id())));
        if (partnershipDTO.enterprise() != null)
            existing.setEnterprise(enterpriseRepository.findById(partnershipDTO.enterprise().id())
                    .orElseThrow(() -> new ResourceNotFoundException("Enterprise", "ID", partnershipDTO.enterprise().id())));
        if (partnershipDTO.active() != null) existing.setActive(partnershipDTO.active());

        var updatedPartnership = partnershipRepository.saveAndFlush(existing);

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