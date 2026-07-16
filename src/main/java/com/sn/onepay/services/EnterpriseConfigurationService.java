package com.sn.onepay.services;

import com.sn.onepay.dto.EnterpriseConfigurationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface EnterpriseConfigurationService {

    EnterpriseConfigurationDTO createEnterpriseConfiguration(EnterpriseConfigurationDTO enterpriseConfigurationDTO);

    EnterpriseConfigurationDTO updateEnterpriseConfiguration(EnterpriseConfigurationDTO enterpriseConfigurationDTO, Long enterpriseConfigurationId);

    void deleteEnterpriseConfiguration(Long enterpriseConfigurationId);

    Page<EnterpriseConfigurationDTO> getEnterpriseConfigurationsByFilters(Long id, Long enterpriseId, Double maxAmountRestauration, Double maxAmountMarket, Double maxAmountGasStation, Double maxAmountTelephony, Integer enterprisePercentage, Integer employeePercentage, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);

    EnterpriseConfigurationDTO getEnterpriseConfigurationByEnterpriseId(Long enterpriseId);
}
