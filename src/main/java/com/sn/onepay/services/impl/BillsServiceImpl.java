package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.BillsDTO;
import com.sn.onepay.entity.Bills;
import com.sn.onepay.entity.QBills;
import com.sn.onepay.enumeration.BillStatus;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.BillsMapper;
import com.sn.onepay.repository.BillsRepository;
import com.sn.onepay.services.BillsService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class BillsServiceImpl implements BillsService {

    final BillsRepository billsRepository;
    final BillsMapper billsMapper;

    @Override
    public BillsDTO createBills(BillsDTO billsDTO) {

        Bills bills = billsMapper.asEntity(billsDTO);
        bills.setRef(UUID.randomUUID().toString());
        bills.setActive(true);
        var savedBills = billsRepository.save(bills);

        log.info("Created new Bills: {}", savedBills);
        log.trace("Created new Bills with id: {}", savedBills.getId());

        return billsMapper.asDTO(savedBills);
    }

    @Override
    public BillsDTO updateBills(BillsDTO billsDTO, Long billsId) {

        Bills existing = billsRepository.findById(billsId)
                .orElseThrow(() -> new ResourceNotFoundException("Bills", "ID", billsId));

        if (billsDTO.startDate() != null) existing.setStartDate(billsDTO.startDate());
        if (billsDTO.endDate() != null) existing.setEndDate(billsDTO.endDate());
        if (billsDTO.totalAmount() != null) existing.setTotalAmount(billsDTO.totalAmount());
        if (billsDTO.billStatus() != null) existing.setBillStatus(billsDTO.billStatus());
        if (billsDTO.period() != null) existing.setPeriod(billsDTO.period());
        if (billsDTO.active() != null) existing.setActive(billsDTO.active());

        var updated = billsRepository.saveAndFlush(existing);

        log.info("Updated Bills: {}", updated);
        log.trace("Updated Bills with id: {}", updated.getId());

        return billsMapper.asDTO(updated);
    }

    @Override
    public void deleteBills(Long billsId) {

        /*Implements logical deletion*/
        Bills bills = billsRepository.findById(billsId).orElseThrow(() -> new ResourceNotFoundException("Bills", "ID", billsId));
        bills.setActive(false);
        billsRepository.saveAndFlush(bills);

        log.info("Deleted Bills: {}", billsId);
        log.trace("Deleted Bills with id: {}", billsId);
    }

    @Override
    public Page<BillsDTO> getBillsByFilters(Long id, String ref, Long partnershipId, BillStatus billStatus, String period, Boolean active, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable) {

        QBills bills = QBills.bills;
        BooleanBuilder builder = new BooleanBuilder();

        if (id != null) {
            builder.and(bills.id.eq(id));
        }
        if (ref != null && !ref.isEmpty()) {
            builder.and(bills.ref.containsIgnoreCase(ref));
        }
        if (partnershipId != null) {
            builder.and(bills.partnership.id.eq(partnershipId));
        }
        if (billStatus != null) {
            builder.and(bills.billStatus.eq(billStatus));
        }
        if (period != null && !period.isEmpty()) {
            builder.and(bills.period.eq(period));
        }
        if (active != null) {
            builder.and(bills.active.eq(active));
        }
        if (startDate != null) {
            builder.and(bills.startDate.goe(startDate));
        }
        if (endDate != null) {
            builder.and(bills.endDate.loe(endDate));
        }
        if (creationDate != null) {
            builder.and(bills.creationDate.goe(creationDate));
        }
        if (modificationDate != null) {
            builder.and(bills.modificationDate.loe(modificationDate));
        }

        Page<Bills> result = billsRepository.findAll(builder, pageable);
        return result.map(billsMapper::asDTO);
    }
}
