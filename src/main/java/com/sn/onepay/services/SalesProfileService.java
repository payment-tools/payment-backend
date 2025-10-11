package com.sn.onepay.services;

import com.sn.onepay.dto.SalesProfileDTO;
import com.sn.onepay.entity.Sales;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.enumeration.StateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface SalesProfileService {
    
    SalesProfileDTO createSalesProfile(SalesProfileDTO salesProfileDTO);

    SalesProfileDTO updateSalesProfile(SalesProfileDTO salesProfileDTO, Long salesProfileId);

    void deleteSalesProfile(Long salesProfileId);

    Page<SalesProfileDTO> getSalesProfilesByFilters(Long id, String ref, String firstname, String lastname, String username, String email, String phoneNumber, Roles role, Sales sales, StateStatus status, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);
}
