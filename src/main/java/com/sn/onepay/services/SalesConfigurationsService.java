package com.sn.onepay.services;

import com.sn.onepay.dto.SalesConfigurationsCreateDTO;
import com.sn.onepay.dto.SalesConfigurationsDTO;
import com.sn.onepay.dto.SalesConfigurationsUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface SalesConfigurationsService {

    SalesConfigurationsDTO createSalesConfigurations(SalesConfigurationsCreateDTO salesConfigurationsCreateDTO);

    SalesConfigurationsDTO updateSalesConfigurations(SalesConfigurationsUpdateDTO salesConfigurationsUpdateDTO, Long salesConfigurationsId);

    void deleteSalesConfigurations(Long salesConfigurationsId);


    Page<SalesConfigurationsDTO> getSalesConfigurationsByFilters(Long id, Long salesId, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);

    SalesConfigurationsDTO getSalesConfigurationsBySalesId(Long salesId);

    SalesConfigurationsDTO getSalesConfigurationsById(Long id);
}
