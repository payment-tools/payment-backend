package com.sn.onepay.services.impl;

import com.sn.onepay.dto.EnterpriseConfigurationDTO;
import com.sn.onepay.exceptions.ResourceAlreadyExistException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.EnterpriseConfigurationMapper;
import com.sn.onepay.repository.EnterpriseConfigurationRepository;
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

    final EnterpriseConfigurationRepository enterpriseConfigurationRepository;
    final EnterpriseConfigurationMapper enterpriseConfigurationMapper;

    @Override
    public EnterpriseConfigurationDTO createEnterpriseConfiguration(EnterpriseConfigurationDTO enterpriseConfigurationDTO) {

        var savedEnterpriseConfiguration = enterpriseConfigurationRepository.save(enterpriseConfigurationMapper.asEntity(enterpriseConfigurationDTO));

        log.info("Created new enterprise configuration: {}", savedEnterpriseConfiguration);
        log.trace("Created new enterprise configuration with id: {}", savedEnterpriseConfiguration.getId());

        return enterpriseConfigurationMapper.asDTO(savedEnterpriseConfiguration);

    }

    @Override
    public EnterpriseConfigurationDTO updateEnterpriseConfiguration(EnterpriseConfigurationDTO enterpriseConfigurationDTO, Long enterpriseConfigurationId) {

        enterpriseConfigurationRepository.findById(enterpriseConfigurationId).orElseThrow( () -> new ResourceNotFoundException("Enterprise Configuration", "ID", enterpriseConfigurationId));

        var updatedEnterpriseConfiguration = enterpriseConfigurationRepository.saveAndFlush(enterpriseConfigurationMapper.asEntity(enterpriseConfigurationDTO));

        log.info("Updated enterprise configuration: {}", updatedEnterpriseConfiguration);
        log.trace("Updated enterprise configuration with id: {}", updatedEnterpriseConfiguration.getId());

        return enterpriseConfigurationMapper.asDTO(updatedEnterpriseConfiguration);
    }

    @Override
    public void deleteEnterpriseConfiguration(Long enterpriseConfigurationId) {

        enterpriseConfigurationRepository.findById(enterpriseConfigurationId).orElseThrow( () -> new ResourceNotFoundException("Enterprise Configuration", "ID", enterpriseConfigurationId));

        enterpriseConfigurationRepository.deleteById(enterpriseConfigurationId);

        log.info("Deleted enterprise configuration with id: {}", enterpriseConfigurationId);
        log.trace("Deleted enterprise configuration with id: {}", enterpriseConfigurationId);

    }

    @Override
    public Page<EnterpriseConfigurationDTO> getEnterpriseConfigurationsByFilter(EnterpriseConfigurationDTO enterpriseConfigurationDTO, Pageable pageable) {
        return null;
    }

    @Override
    public EnterpriseConfigurationDTO getEnterpriseConfigurationByEnterpriseId(Long enterpriseId) {
        return enterpriseConfigurationMapper.asDTO(enterpriseConfigurationRepository.getEnterpriseConfigurationByEnterpriseId(enterpriseId));
    }

}
