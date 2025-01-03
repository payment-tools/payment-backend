package com.sn.onepay.services;

import com.sn.onepay.dto.PartnershipDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PartnershipService {
    
    PartnershipDTO createPartnership(PartnershipDTO partnershipDTO);

    PartnershipDTO updatePartnership(PartnershipDTO partnershipDTO, Long partnershipId);

    void deletePartnership(Long partnershipId);

    Page<PartnershipDTO> getPartnershipsByFilter(PartnershipDTO partnershipDTO, Pageable pageable);

    PartnershipDTO getPartnershipsBySalesIdAndEnterpriseId(Long salesId, Long enterpriseId);
}
