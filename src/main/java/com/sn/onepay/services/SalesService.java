package com.sn.onepay.services;

import com.sn.onepay.dto.SalesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SalesService {

    SalesDTO createSales(SalesDTO salesDTO);

    SalesDTO updateSales(SalesDTO salesDTO, Long salesId);

    void deleteSales(Long salesId);

    Page<SalesDTO> getSalesByFilter(SalesDTO salesDTO, Pageable pageable);
}
