package com.sn.onepay.services;

import com.sn.onepay.dto.CashierCreateDTO;
import com.sn.onepay.dto.CashierDTO;
import com.sn.onepay.dto.CashierUpdateDTO;
import com.sn.onepay.enumeration.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;


public interface CashierService {

    CashierDTO createCashier(CashierCreateDTO cashierCreateDTO);

    CashierDTO updateCashier(CashierUpdateDTO cashierUpdateDTO, Long cashierId);

    void deleteCashier(Long id);

    Page<CashierDTO> getCashiersByFilter(Long id, String ref, String firstname, String lastname, String username, String email, String phoneNumber, Roles role, Long salesId, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);

    CashierDTO getCashierById(Long id);

}
