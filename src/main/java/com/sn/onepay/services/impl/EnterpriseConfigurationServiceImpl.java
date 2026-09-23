package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.EnterpriseConfigurationCreateDTO;
import com.sn.onepay.dto.EnterpriseConfigurationDTO;
import com.sn.onepay.dto.EnterpriseConfigurationUpdateDTO;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.entity.EnterpriseConfiguration;
import com.sn.onepay.entity.QEnterpriseConfiguration;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.EnterpriseConfigurationMapper;
import com.sn.onepay.repository.EnterpriseConfigurationRepository;
import com.sn.onepay.repository.EnterpriseRepository;
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
    final EnterpriseRepository enterpriseRepository;
    final EnterpriseConfigurationMapper enterpriseConfigurationMapper;

    @Override
    public EnterpriseConfigurationDTO createEnterpriseConfiguration(EnterpriseConfigurationCreateDTO enterpriseConfigurationCreateDTO) {

        Enterprise enterprise = enterpriseRepository.findById(enterpriseConfigurationCreateDTO.enterpriseId())
                .orElseThrow(() -> new ResourceNotFoundException("Enterprise", "ID", enterpriseConfigurationCreateDTO.enterpriseId()));

        EnterpriseConfiguration enterpriseConfiguration = new EnterpriseConfiguration();
        enterpriseConfiguration.setEnterprise(enterprise);
        enterpriseConfiguration.setMaxAmountRestauration(enterpriseConfigurationCreateDTO.maxAmountRestauration());
        enterpriseConfiguration.setMaxAmountMarket(enterpriseConfigurationCreateDTO.maxAmountMarket());
        enterpriseConfiguration.setMaxAmountGasStation(enterpriseConfigurationCreateDTO.maxAmountGasStation());
        enterpriseConfiguration.setMaxAmountTelephony(enterpriseConfigurationCreateDTO.maxAmountTelephony());
        enterpriseConfiguration.setEnterprisePercentage(enterpriseConfigurationCreateDTO.enterprisePercentage());
        enterpriseConfiguration.setEmployeePercentage(enterpriseConfigurationCreateDTO.employeePercentage());
        enterpriseConfiguration.setActive(true);

        var savedEnterpriseConfiguration = enterpriseConfigurationRepository.save(enterpriseConfiguration);

        log.info("Created new enterprise configuration: {}", savedEnterpriseConfiguration);
        log.trace("Created new enterprise configuration with id: {}", savedEnterpriseConfiguration.getId());

        return enterpriseConfigurationMapper.asDTO(savedEnterpriseConfiguration);

    }

    @Override
    public EnterpriseConfigurationDTO updateEnterpriseConfiguration(EnterpriseConfigurationUpdateDTO enterpriseConfigurationUpdateDTO, Long enterpriseConfigurationId) {

        EnterpriseConfiguration existing = enterpriseConfigurationRepository.findById(enterpriseConfigurationId).orElseThrow( () -> new ResourceNotFoundException("Enterprise Configuration", "ID", enterpriseConfigurationId));

        if (enterpriseConfigurationUpdateDTO.maxAmountRestauration() != null) existing.setMaxAmountRestauration(enterpriseConfigurationUpdateDTO.maxAmountRestauration());
        if (enterpriseConfigurationUpdateDTO.maxAmountMarket() != null) existing.setMaxAmountMarket(enterpriseConfigurationUpdateDTO.maxAmountMarket());
        if (enterpriseConfigurationUpdateDTO.maxAmountGasStation() != null) existing.setMaxAmountGasStation(enterpriseConfigurationUpdateDTO.maxAmountGasStation());
        if (enterpriseConfigurationUpdateDTO.maxAmountTelephony() != null) existing.setMaxAmountTelephony(enterpriseConfigurationUpdateDTO.maxAmountTelephony());
        if (enterpriseConfigurationUpdateDTO.enterprisePercentage() != null) existing.setEnterprisePercentage(enterpriseConfigurationUpdateDTO.enterprisePercentage());
        if (enterpriseConfigurationUpdateDTO.employeePercentage() != null) existing.setEmployeePercentage(enterpriseConfigurationUpdateDTO.employeePercentage());
        if (enterpriseConfigurationUpdateDTO.active() != null) existing.setActive(enterpriseConfigurationUpdateDTO.active());

        var updatedEnterpriseConfiguration = enterpriseConfigurationRepository.saveAndFlush(existing);

        log.info("Updated enterprise configuration: {}", updatedEnterpriseConfiguration);
        log.trace("Updated enterprise configuration with id: {}", updatedEnterpriseConfiguration.getId());

        return enterpriseConfigurationMapper.asDTO(updatedEnterpriseConfiguration);
    }

    @Override
    public void deleteEnterpriseConfiguration(Long enterpriseConfigurationId) {

        EnterpriseConfiguration enterpriseConfiguration = enterpriseConfigurationRepository.findById(enterpriseConfigurationId).orElseThrow( () -> new ResourceNotFoundException("Enterprise Configuration", "ID", enterpriseConfigurationId));

        /*Implements logical deletion*/
        enterpriseConfiguration.setActive(false);
        enterpriseConfigurationRepository.saveAndFlush(enterpriseConfiguration);

        log.info("Deleted enterprise configuration with id: {}", enterpriseConfigurationId);
        log.trace("Deleted enterprise configuration with id: {}", enterpriseConfigurationId);

    }

    @Override
    public Page<EnterpriseConfigurationDTO> getEnterpriseConfigurationsByFilters(Long id, Long enterpriseId, Double maxAmountRestauration, Double maxAmountMarket, Double maxAmountGasStation, Double maxAmountTelephony, Integer enterprisePercentage, Integer employeePercentage, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable) {

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
        if (active != null) {
            builder.and(enterpriseConfiguration.active.eq(active));
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

    @Override
    public EnterpriseConfigurationDTO getEnterpriseConfigurationById(Long id) {
        return enterpriseConfigurationMapper.asDTO(enterpriseConfigurationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Enterprise Configuration", "ID", id)));
    }

}
