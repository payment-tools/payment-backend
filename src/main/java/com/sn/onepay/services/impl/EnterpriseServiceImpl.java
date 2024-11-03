package com.sn.onepay.services.impl;

import com.sn.onepay.dto.EnterpriseDTO;
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
    @Override
    public EnterpriseDTO createEnterprise(EnterpriseDTO enterpriseDTO) {
        return null;
    }

    @Override
    public EnterpriseDTO updateEnterprise(EnterpriseDTO enterpriseDTO, Long enterpriseId) {
        return null;
    }

    @Override
    public void deleteEnterprise(Long id) {

    }

    @Override
    public Page<EnterpriseDTO> getEnterprisesByFilter(EnterpriseDTO enterpriseDTO, Pageable pageable) {
        return null;
    }
}
