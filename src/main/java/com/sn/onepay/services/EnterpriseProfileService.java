package com.sn.onepay.services;

import com.sn.onepay.dto.EnterpriseProfileDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnterpriseProfileService {

    EnterpriseProfileDTO createEnterpriseProfile(EnterpriseProfileDTO enterpriseProfileDTO);

    EnterpriseProfileDTO updateEnterpriseProfile(EnterpriseProfileDTO enterpriseProfileDTO, Long enterpriseProfileId);

    void deleteEnterpriseProfile(Long id);

    Page<EnterpriseProfileDTO> getEnterpriseProfilesByFilter(EnterpriseProfileDTO enterpriseProfileDTO, Pageable pageable);
}
