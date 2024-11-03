package com.sn.onepay.services;

import com.sn.onepay.dto.PartnershipDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PartnershipService {
    
    PartnershipDTO createPartnership(PartnershipDTO partnershipDTO);

    PartnershipDTO updatePartnership(PartnershipDTO partnershipDTO, Long partnershipId);

    void deletePartnership(Long id);

    Page<PartnershipDTO> getPartnershipsByFilter(PartnershipDTO partnershipDTO, Pageable pageable);
    
}
