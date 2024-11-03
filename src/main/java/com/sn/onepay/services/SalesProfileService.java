package com.sn.onepay.services;

import com.sn.onepay.dto.SalesProfileDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SalesProfileService {
    
    SalesProfileDTO createSalesProfile(SalesProfileDTO salesProfileDTO);

    SalesProfileDTO updateSalesProfile(SalesProfileDTO salesProfileDTO, Long salesProfileId);

    void deleteSalesProfile(Long id);

    Page<SalesProfileDTO> getSalesProfilesByFilter(SalesProfileDTO salesProfileDTO, Pageable pageable);
}
