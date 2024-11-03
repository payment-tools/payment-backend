package com.sn.onepay.services.impl;

import com.sn.onepay.dto.SalesConfigurationsDTO;
import com.sn.onepay.services.SalesConfigurationsService;
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
public class SalesConfigurationsServiceImpl implements SalesConfigurationsService {
    @Override
    public SalesConfigurationsDTO createSalesConfigurations(SalesConfigurationsDTO salesConfigurationsDTO) {
        return null;
    }

    @Override
    public SalesConfigurationsDTO updateSalesConfigurations(SalesConfigurationsDTO salesConfigurationsDTO, Long salesConfigurationsId) {
        return null;
    }

    @Override
    public void deleteSalesConfigurations(Long id) {

    }

    @Override
    public Page<SalesConfigurationsDTO> getSalesConfigurationssByFilter(SalesConfigurationsDTO salesConfigurationsDTO, Pageable pageable) {
        return null;
    }
}
