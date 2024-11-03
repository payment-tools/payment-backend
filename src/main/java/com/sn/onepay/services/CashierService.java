package com.sn.onepay.services;

import com.sn.onepay.dto.CashierDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CashierService {

    CashierDTO createCashier(CashierDTO cashierDTO);

    CashierDTO updateCashier(CashierDTO cashierDTO, Long cashierId);

    void deleteCashier(Long id);

    Page<CashierDTO> getCashiersByFilter(CashierDTO cashierDTO, Pageable pageable);

}
