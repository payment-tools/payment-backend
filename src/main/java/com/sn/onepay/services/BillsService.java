package com.sn.onepay.services;

import com.sn.onepay.dto.BillsDTO;
import com.sn.onepay.enumeration.BillStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface BillsService {

    BillsDTO createBills(BillsDTO billsDTO);

    BillsDTO updateBills(BillsDTO billsDTO, Long billsId);

    void deleteBills(Long billsId);

    Page<BillsDTO> getBillsByFilters(Long id, String ref, Long partnershipId, BillStatus billStatus, String period, Boolean active, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);
}
