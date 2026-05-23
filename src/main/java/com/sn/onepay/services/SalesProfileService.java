package com.sn.onepay.services;

import com.sn.onepay.dto.SalesProfileDTO;
import com.sn.onepay.enumeration.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface SalesProfileService {

    SalesProfileDTO createSalesProfile(SalesProfileDTO salesProfileDTO);

    SalesProfileDTO updateSalesProfile(SalesProfileDTO salesProfileDTO, Long salesProfileId);

    void deleteSalesProfile(Long salesProfileId);

    Page<SalesProfileDTO> getSalesProfilesByFilters(Long id, String ref, String firstname, String lastname, String username, String email, String phoneNumber, Roles role, Long salesId, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);
}
