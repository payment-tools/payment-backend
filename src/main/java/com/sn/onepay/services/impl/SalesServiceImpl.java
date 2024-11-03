package com.sn.onepay.services.impl;

import com.sn.onepay.dto.SalesDTO;
import com.sn.onepay.services.SalesService;
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
public class SalesServiceImpl implements SalesService {
    @Override
    public SalesDTO createSales(SalesDTO salesDTO) {
        return null;
    }

    @Override
    public SalesDTO updateSales(SalesDTO salesDTO, Long salesId) {
        return null;
    }

    @Override
    public void deleteSales(Long id) {

    }

    @Override
    public Page<SalesDTO> getSalesByFilter(SalesDTO salesDTO, Pageable pageable) {
        return null;
    }
}
