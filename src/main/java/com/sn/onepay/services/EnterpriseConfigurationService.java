package com.sn.onepay.services;

import com.sn.onepay.dto.EnterpriseConfigurationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnterpriseConfigurationService {

    EnterpriseConfigurationDTO createEnterpriseConfiguration(EnterpriseConfigurationDTO enterpriseConfigurationDTO);

    EnterpriseConfigurationDTO updateEnterpriseConfiguration(EnterpriseConfigurationDTO enterpriseConfigurationDTO, Long enterpriseConfigurationId);

    void deleteEnterpriseConfiguration(Long enterpriseConfigurationId);

    Page<EnterpriseConfigurationDTO> getEnterpriseConfigurationsByFilter(EnterpriseConfigurationDTO enterpriseConfigurationDTO, Pageable pageable);
}
