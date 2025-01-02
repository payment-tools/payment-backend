package com.sn.onepay.services.impl;

import com.sn.onepay.dto.EnterpriseDTO;
import com.sn.onepay.exceptions.ResourceAlreadyExistException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.EnterpriseMapper;
import com.sn.onepay.repository.EnterpriseRepository;
import com.sn.onepay.services.EnterpriseService;
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
public class EnterpriseServiceImpl implements EnterpriseService {

    final EnterpriseRepository enterpriseRepository;
    final EnterpriseMapper enterpriseMapper;

    @Override
    public EnterpriseDTO createEnterprise(EnterpriseDTO enterpriseDTO) {

        // TODO set actual Quota and make verifcation
        var savedEnterprise = enterpriseRepository.save(enterpriseMapper.asEntity(enterpriseDTO));

        log.info("Created new Enterprise: {}", savedEnterprise);
        log.trace("Created new Enterprise with id: {}", savedEnterprise.getId());

        return enterpriseMapper.asDTO(savedEnterprise);
    }

    @Override
    public EnterpriseDTO updateEnterprise(EnterpriseDTO enterpriseDTO, Long enterpriseId) {

        enterpriseRepository.findById(enterpriseId).orElseThrow(() -> new ResourceNotFoundException("Enterprise", "ID", enterpriseId));

        var updatedEnterprise = enterpriseRepository.saveAndFlush(enterpriseMapper.asEntity(enterpriseDTO));

        log.info("Updated Enterprise: {}", updatedEnterprise);
        log.trace("Updated Enterprise with id: {}", updatedEnterprise.getId());

        return enterpriseMapper.asDTO(updatedEnterprise);

    }

    @Override
    public void deleteEnterprise(Long enterpriseId) {

        enterpriseRepository.findById(enterpriseId).orElseThrow(() -> new ResourceNotFoundException("Enterprise", "ID", enterpriseId));

        enterpriseRepository.deleteById(enterpriseId);

        log.info("Deleted Enterprise: {}", enterpriseId);
        log.trace("Deleted Enterprise with id: {}", enterpriseId);

    }

    @Override
    public Page<EnterpriseDTO> getEnterprisesByFilter(EnterpriseDTO enterpriseDTO, Pageable pageable) {
        return null;
    }
}
