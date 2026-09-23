package com.sn.onepay.services;

import com.sn.onepay.dto.PartnershipCreateDTO;
import com.sn.onepay.dto.PartnershipDTO;
import com.sn.onepay.dto.PartnershipUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface PartnershipService {

    PartnershipDTO createPartnership(PartnershipCreateDTO partnershipCreateDTO);

    PartnershipDTO updatePartnership(PartnershipUpdateDTO partnershipUpdateDTO, Long partnershipId);

    void deletePartnership(Long partnershipId);


    Page<PartnershipDTO> getPartnershipsByFilters(Long id, String ref, Long salesId, Long enterpriseId, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);

    PartnershipDTO getPartnershipsBySalesIdAndEnterpriseId(Long salesId, Long enterpriseId);

    PartnershipDTO getPartnershipById(Long id);
}
