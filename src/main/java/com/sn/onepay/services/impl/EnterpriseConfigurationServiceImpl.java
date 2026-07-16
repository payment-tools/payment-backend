package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.EnterpriseConfigurationDTO;
import com.sn.onepay.entity.EnterpriseConfiguration;
import com.sn.onepay.entity.QEnterpriseConfiguration;
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

import java.time.LocalDateTime;

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
    public Page<EnterpriseConfigurationDTO> getEnterpriseConfigurationsByFilters(Long id, Long enterpriseId, Double maxAmountRestauration, Double maxAmountMarket, Double maxAmountGasStation, Double maxAmountTelephony, Integer enterprisePercentage, Integer employeePercentage, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable) {

        QEnterpriseConfiguration enterpriseConfiguration = QEnterpriseConfiguration.enterpriseConfiguration;
        BooleanBuilder builder = new BooleanBuilder();

        if (id != null) {
            builder.and(enterpriseConfiguration.id.eq(id));
        }
        if (enterpriseId != null) {
            builder.and(enterpriseConfiguration.enterprise.id.eq(enterpriseId));
        }
        if (maxAmountRestauration != null) {
            builder.and(enterpriseConfiguration.maxAmountRestauration.eq(maxAmountRestauration));
        }
        if (maxAmountMarket != null) {
            builder.and(enterpriseConfiguration.maxAmountMarket.eq(maxAmountMarket));
        }
        if (maxAmountGasStation != null) {
            builder.and(enterpriseConfiguration.maxAmountGasStation.eq(maxAmountGasStation));
        }
        if (maxAmountTelephony != null) {
            builder.and(enterpriseConfiguration.maxAmountTelephony.eq(maxAmountTelephony));
        }
        if (enterprisePercentage != null) {
            builder.and(enterpriseConfiguration.enterprisePercentage.eq(enterprisePercentage));
        }
        if (employeePercentage != null) {
            builder.and(enterpriseConfiguration.employeePercentage.eq(employeePercentage));
        }
        if (creationDate != null) {
            builder.and(enterpriseConfiguration.creationDate.goe(creationDate));
        }
        if (modificationDate != null) {
            builder.and(enterpriseConfiguration.modificationDate.loe(modificationDate));
        }

        Page<EnterpriseConfiguration> result = enterpriseConfigurationRepository.findAll(builder, pageable);
        return result.map(enterpriseConfigurationMapper::asDTO);
    }

    @Override
    public EnterpriseConfigurationDTO getEnterpriseConfigurationByEnterpriseId(Long enterpriseId) {
        return enterpriseConfigurationMapper.asDTO(enterpriseConfigurationRepository.getEnterpriseConfigurationByEnterpriseId(enterpriseId));
    }

}
