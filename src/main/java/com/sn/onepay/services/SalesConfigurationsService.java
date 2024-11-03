package com.sn.onepay.services;

import com.sn.onepay.dto.SalesConfigurationsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SalesConfigurationsService {

    SalesConfigurationsDTO createSalesConfigurations(SalesConfigurationsDTO salesConfigurationsDTO);

    SalesConfigurationsDTO updateSalesConfigurations(SalesConfigurationsDTO salesConfigurationsDTO, Long salesConfigurationsId);

    void deleteSalesConfigurations(Long id);

    Page<SalesConfigurationsDTO> getSalesConfigurationssByFilter(SalesConfigurationsDTO salesConfigurationsDTO, Pageable pageable);
}
