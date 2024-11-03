package com.sn.onepay.services.impl;

import com.sn.onepay.dto.EnterpriseProfileDTO;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.EnterpriseProfileMapper;
import com.sn.onepay.repository.EnterpriseProfileRepository;
import com.sn.onepay.services.EnterpriseProfileService;
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
public class EnterpriseProfileServiceImpl implements EnterpriseProfileService {

    final EnterpriseProfileRepository enterpriseProfileRepository;
    final EnterpriseProfileMapper enterpriseProfileMapper;

    @Override
    public EnterpriseProfileDTO createEnterpriseProfile(EnterpriseProfileDTO enterpriseProfileDTO) {

        var savedEnterpriseProfile = enterpriseProfileRepository.save(enterpriseProfileMapper.asEntity(enterpriseProfileDTO));

        log.info("Created Enterprise Profile: {}", savedEnterpriseProfile);
        log.trace("Created Enterprise Profile with id: {}", savedEnterpriseProfile.getId());

        return enterpriseProfileMapper.asDTO(savedEnterpriseProfile);
    }

    @Override
    public EnterpriseProfileDTO updateEnterpriseProfile(EnterpriseProfileDTO enterpriseProfileDTO, Long enterpriseProfileId) {

        enterpriseProfileRepository.findById(enterpriseProfileId).orElseThrow( () -> new ResourceNotFoundException("Enterprise Profile", "ID", enterpriseProfileId));

        var updatedEnterpriseProfile = enterpriseProfileRepository.save(enterpriseProfileMapper.asEntity(enterpriseProfileDTO));

        log.info("Updated Enterprise Profile: {}", updatedEnterpriseProfile);
        log.trace("Updated Enterprise Profile with id: {}", updatedEnterpriseProfile.getId());

        return enterpriseProfileMapper.asDTO(updatedEnterpriseProfile);
    }

    @Override
    public void deleteEnterpriseProfile(Long enterpriseProfileId) {

        enterpriseProfileRepository.findById(enterpriseProfileId).orElseThrow( () -> new ResourceNotFoundException("Enterprise Profile", "ID", enterpriseProfileId));

        enterpriseProfileRepository.deleteById(enterpriseProfileId);

        log.info("Deleted Enterprise Profile: {}", enterpriseProfileId);
        log.trace("Deleted Enterprise Profile with id: {}", enterpriseProfileId);


    }

    @Override
    public Page<EnterpriseProfileDTO> getEnterpriseProfilesByFilter(EnterpriseProfileDTO enterpriseProfileDTO, Pageable pageable) {
        return null;
    }
}
