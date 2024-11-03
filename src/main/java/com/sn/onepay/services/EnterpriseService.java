package com.sn.onepay.services;

import com.sn.onepay.dto.EnterpriseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnterpriseService {

    EnterpriseDTO createEnterprise(EnterpriseDTO enterpriseDTO);

    EnterpriseDTO updateEnterprise(EnterpriseDTO enterpriseDTO, Long enterpriseId);

    void deleteEnterprise(Long id);

    Page<EnterpriseDTO> getEnterprisesByFilter(EnterpriseDTO enterpriseDTO, Pageable pageable);
}
