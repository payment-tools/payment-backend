package com.sn.onepay.services.impl;

import com.sn.onepay.dto.CashierDTO;
import com.sn.onepay.entity.Cashier;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.CashierMapper;
import com.sn.onepay.repository.CashierRepository;
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

    final CashierRepository cashierRepository;
    final CashierMapper cashierMapper;

    @Override
    public CashierDTO createCashier(CashierDTO cashierDTO) {

        Cashier cashier = cashierMapper.asEntity(cashierDTO);
        cashier.setActive(true);
        var savedCashier = cashierRepository.save(cashier);

        log.info("Created new cashier: {}", savedCashier);
        log.trace("Saved new cashier with id: {}", savedCashier.getId());

        return cashierMapper.asDTO(savedCashier);
    }

    @Override
    public CashierDTO updateCashier(CashierDTO cashierDTO, Long cashierId) {

        cashierRepository.findById(cashierId).orElseThrow( () -> new ResourceNotFoundException("Cashier", "ID", cashierId));

        var updatedCashier = cashierRepository.saveAndFlush(cashierMapper.asEntity(cashierDTO));

        log.info("Updated cashier: {}", updatedCashier);
        log.trace("Updated cashier with id: {}", updatedCashier.getId());

        return cashierMapper.asDTO(updatedCashier);
    }

    @Override
    public void deleteCashier(Long cashierId) {

        Cashier cashier = cashierRepository.findById(cashierId).orElseThrow( () -> new ResourceNotFoundException("Cashier", "ID", cashierId));
        cashier.setActive(false);
        cashierRepository.saveAndFlush(cashier);

        log.info("Deleted cashier: {}", cashierId);
        log.trace("Deleted cashier with id: {}", cashierId);

    }

    @Override
    public Page<CashierDTO> getCashiersByFilter(CashierDTO cashierDTO, Pageable pageable) {
        return null;
    }


}