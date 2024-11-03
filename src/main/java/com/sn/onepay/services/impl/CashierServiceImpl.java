package com.sn.onepay.services.impl;

import com.sn.onepay.dto.CashierDTO;
import com.sn.onepay.services.CashierService;
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
public class CashierServiceImpl implements CashierService {


    @Override
    public CashierDTO createCashier(CashierDTO cashierDTO) {
        return null;
    }

    @Override
    public CashierDTO updateCashier(CashierDTO cashierDTO, Long cashierId) {
        return null;
    }

    @Override
    public void deleteCashier(Long id) {

    }

    @Override
    public Page<CashierDTO> getCashiersByFilter(CashierDTO cashierDTO, Pageable pageable) {
        return null;
    }


}
