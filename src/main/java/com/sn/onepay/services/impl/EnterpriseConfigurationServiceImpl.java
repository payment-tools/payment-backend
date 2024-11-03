package com.sn.onepay.services.impl;

import com.sn.onepay.dto.EnterpriseConfigurationDTO;
import com.sn.onepay.services.EnterpriseConfigurationService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class EnterpriseConfigurationServiceImpl implements EnterpriseConfigurationService {
    @Override
    public EnterpriseConfigurationDTO createEnterpriseConfiguration(EnterpriseConfigurationDTO enterpriseConfigurationDTO) {
        return null;
    }

    @Override
    public EnterpriseConfigurationDTO updateEnterpriseConfiguration(EnterpriseConfigurationDTO enterpriseConfigurationDTO, Long enterpriseConfigurationId) {
        return null;
    }

    @Override
    public void deleteEnterpriseConfiguration(Long id) {

    }

    @Override
    public Page<EnterpriseConfigurationDTO> getEnterpriseConfigurationsByFilter(EnterpriseConfigurationDTO enterpriseConfigurationDTO, Pageable pageable) {
        return null;
    }
}
