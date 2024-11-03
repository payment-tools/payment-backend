package com.sn.onepay.services.impl;

import com.sn.onepay.dto.SalesProfileDTO;
import com.sn.onepay.services.SalesProfileService;
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
public class SalesProfileServiceImpl implements SalesProfileService {
    @Override
    public SalesProfileDTO createSalesProfile(SalesProfileDTO salesProfileDTO) {
        return null;
    }

    @Override
    public SalesProfileDTO updateSalesProfile(SalesProfileDTO salesProfileDTO, Long salesProfileId) {
        return null;
    }

    @Override
    public void deleteSalesProfile(Long id) {

    }

    @Override
    public Page<SalesProfileDTO> getSalesProfilesByFilter(SalesProfileDTO salesProfileDTO, Pageable pageable) {
        return null;
    }
}
