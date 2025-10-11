package com.sn.onepay.services;

import com.sn.onepay.dto.EnterpriseDTO;
import com.sn.onepay.enumeration.Modules;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface EnterpriseService {

    EnterpriseDTO createEnterprise(EnterpriseDTO enterpriseDTO);

    EnterpriseDTO updateEnterprise(EnterpriseDTO enterpriseDTO, Long enterpriseId);

    void deleteEnterprise(Long enterpriseId);

    Page<EnterpriseDTO> getEnterprisesByFilters(Long id, String ref, String name, Long maxQuota, Long actualQuota, String address, Modules enrolledModules, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);
}
