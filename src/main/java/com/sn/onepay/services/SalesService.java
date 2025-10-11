package com.sn.onepay.services;

import com.sn.onepay.dto.SalesDTO;
import com.sn.onepay.enumeration.Modules;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface SalesService {

    SalesDTO createSales(SalesDTO salesDTO);

    SalesDTO updateSales(SalesDTO salesDTO, Long salesId);

    void deleteSales(Long salesId);

    Page<SalesDTO> getSalesByFilters(Long id, String ref, String name, Modules type, String address, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);
}
