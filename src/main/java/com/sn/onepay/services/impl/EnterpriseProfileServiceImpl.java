package com.sn.onepay.services.impl;

import com.sn.onepay.dto.EnterpriseProfileDTO;
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
    @Override
    public EnterpriseProfileDTO createEnterpriseProfile(EnterpriseProfileDTO enterpriseProfileDTO) {
        return null;
    }

    @Override
    public EnterpriseProfileDTO updateEnterpriseProfile(EnterpriseProfileDTO enterpriseProfileDTO, Long enterpriseProfileId) {
        return null;
    }

    @Override
    public void deleteEnterpriseProfile(Long id) {

    }

    @Override
    public Page<EnterpriseProfileDTO> getEnterpriseProfilesByFilter(EnterpriseProfileDTO enterpriseProfileDTO, Pageable pageable) {
        return null;
    }
}
