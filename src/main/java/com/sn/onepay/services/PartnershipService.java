package com.sn.onepay.services;

import com.sn.onepay.dto.PartnershipDTO;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.entity.Sales;
import com.sn.onepay.enumeration.StateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface PartnershipService {
    
    PartnershipDTO createPartnership(PartnershipDTO partnershipDTO);

    PartnershipDTO updatePartnership(PartnershipDTO partnershipDTO, Long partnershipId);

    void deletePartnership(Long partnershipId);


    Page<PartnershipDTO> getPartnershipsByFilters(Long id, String ref, Sales sales, Enterprise enterprise, StateStatus status, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);

    PartnershipDTO getPartnershipsBySalesIdAndEnterpriseId(Long salesId, Long enterpriseId);
}
