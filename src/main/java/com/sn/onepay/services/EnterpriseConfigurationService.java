package com.sn.onepay.services;

import com.sn.onepay.dto.EnterpriseConfigurationDTO;
import com.sn.onepay.entity.Enterprise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface EnterpriseConfigurationService {

    EnterpriseConfigurationDTO createEnterpriseConfiguration(EnterpriseConfigurationDTO enterpriseConfigurationDTO);

    EnterpriseConfigurationDTO updateEnterpriseConfiguration(EnterpriseConfigurationDTO enterpriseConfigurationDTO, Long enterpriseConfigurationId);

    void deleteEnterpriseConfiguration(Long enterpriseConfigurationId);

    Page<EnterpriseConfigurationDTO> getEnterpriseConfigurationsByFilters(Long id, Enterprise enterprise, Double maxAmountRestauration, Double maxAmountMarket, Double maxAmountGasStation, Double maxAmountTelephony, int enterprisePercentage, int employeePercentage, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);

    EnterpriseConfigurationDTO getEnterpriseConfigurationByEnterpriseId(Long enterpriseId);
}
