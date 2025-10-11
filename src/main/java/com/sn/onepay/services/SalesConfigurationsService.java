package com.sn.onepay.services;

import com.sn.onepay.dto.SalesConfigurationsDTO;
import com.sn.onepay.entity.Sales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface SalesConfigurationsService {

    SalesConfigurationsDTO createSalesConfigurations(SalesConfigurationsDTO salesConfigurationsDTO);

    SalesConfigurationsDTO updateSalesConfigurations(SalesConfigurationsDTO salesConfigurationsDTO, Long salesConfigurationsId);

    void deleteSalesConfigurations(Long salesConfigurationsId);


    Page<SalesConfigurationsDTO> getSalesConfigurationsByFilters(Long id, Sales sales, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);

    SalesConfigurationsDTO getSalesConfigurationsBySalesId(Long salesId);
}
