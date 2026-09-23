package com.sn.onepay.services;

import com.sn.onepay.dto.EnterpriseProfileCreateDTO;
import com.sn.onepay.dto.EnterpriseProfileDTO;
import com.sn.onepay.dto.EnterpriseProfileUpdateDTO;
import com.sn.onepay.enumeration.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface EnterpriseProfileService {

    EnterpriseProfileDTO createEnterpriseProfile(EnterpriseProfileCreateDTO enterpriseProfileCreateDTO);

    EnterpriseProfileDTO updateEnterpriseProfile(EnterpriseProfileUpdateDTO enterpriseProfileUpdateDTO, Long enterpriseProfileId);

    void deleteEnterpriseProfile(Long enterpriseProfileId);

    Page<EnterpriseProfileDTO> getEnterpriseProfilesByFilters(Long id, String ref, String firstname, String lastname, String username, String email, String phoneNumber, Roles role, Long enterpriseId, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);

    EnterpriseProfileDTO getMyProfile(String username);

    EnterpriseProfileDTO getEnterpriseProfileById(Long id);
}
