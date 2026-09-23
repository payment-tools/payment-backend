package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.CashierCreateDTO;
import com.sn.onepay.dto.CashierDTO;
import com.sn.onepay.dto.CashierUpdateDTO;
import com.sn.onepay.entity.Cashier;
import com.sn.onepay.entity.QCashier;
import com.sn.onepay.entity.Sales;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.CashierMapper;
import com.sn.onepay.repository.CashierRepository;
import com.sn.onepay.repository.SalesRepository;
import com.sn.onepay.services.CashierService;
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
public class CashierServiceImpl implements CashierService {

    final CashierRepository cashierRepository;
    final SalesRepository salesRepository;
    final CashierMapper cashierMapper;

    @Override
    public CashierDTO createCashier(CashierCreateDTO cashierCreateDTO) {

        Sales sales = salesRepository.findById(cashierCreateDTO.salesId())
                .orElseThrow(() -> new ResourceNotFoundException("Sales", "ID", cashierCreateDTO.salesId()));

        Cashier cashier = new Cashier();
        cashier.setRef(UUID.randomUUID().toString());
        cashier.setFirstname(cashierCreateDTO.firstname());
        cashier.setLastname(cashierCreateDTO.lastname());
        cashier.setUsername(cashierCreateDTO.username());
        cashier.setEmail(cashierCreateDTO.email());
        cashier.setPhoneNumber(cashierCreateDTO.phoneNumber());
        cashier.setRole(cashierCreateDTO.role());
        cashier.setSales(sales);
        cashier.setActive(true);
        var savedCashier = cashierRepository.save(cashier);

        log.info("Created new cashier: {}", savedCashier);
        log.trace("Saved new cashier with id: {}", savedCashier.getId());

        return cashierMapper.asDTO(savedCashier);
    }

    @Override
    public CashierDTO updateCashier(CashierUpdateDTO cashierUpdateDTO, Long cashierId) {

        Cashier existing = cashierRepository.findById(cashierId).orElseThrow( () -> new ResourceNotFoundException("Cashier", "ID", cashierId));

        if (cashierUpdateDTO.firstname() != null) existing.setFirstname(cashierUpdateDTO.firstname());
        if (cashierUpdateDTO.lastname() != null) existing.setLastname(cashierUpdateDTO.lastname());
        if (cashierUpdateDTO.username() != null) existing.setUsername(cashierUpdateDTO.username());
        if (cashierUpdateDTO.email() != null) existing.setEmail(cashierUpdateDTO.email());
        if (cashierUpdateDTO.phoneNumber() != null) existing.setPhoneNumber(cashierUpdateDTO.phoneNumber());
        if (cashierUpdateDTO.role() != null) existing.setRole(cashierUpdateDTO.role());
        if (cashierUpdateDTO.active() != null) existing.setActive(cashierUpdateDTO.active());

        var updatedCashier = cashierRepository.saveAndFlush(existing);

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
    public Page<CashierDTO> getCashiersByFilter(Long id, String ref, String firstname, String lastname, String username, String email, String phoneNumber, Roles role, Long salesId, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable) {

        QCashier cashier = QCashier.cashier;
        BooleanBuilder builder = new BooleanBuilder();

        if (id != null) {
            builder.and(cashier.id.eq(id));
        }
        if (ref != null && !ref.isEmpty()) {
            builder.and(cashier.ref.containsIgnoreCase(ref));
        }
        if (firstname != null && !firstname.isEmpty()) {
            builder.and(cashier.firstname.containsIgnoreCase(firstname));
        }
        if (lastname != null && !lastname.isEmpty()) {
            builder.and(cashier.lastname.containsIgnoreCase(lastname));
        }
        if (username != null && !username.isEmpty()) {
            builder.and(cashier.username.containsIgnoreCase(username));
        }
        if (email != null && !email.isEmpty()) {
            builder.and(cashier.email.containsIgnoreCase(email));
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            builder.and(cashier.phoneNumber.containsIgnoreCase(phoneNumber));
        }
        if (role != null) {
            builder.and(cashier.role.eq(role));
        }
        if (salesId != null) {
            builder.and(cashier.sales.id.eq(salesId));
        }
        if (active != null) {
            builder.and(cashier.active.eq(active));
        }
        if (creationDate != null) {
            builder.and(cashier.creationDate.goe(creationDate));
        }
        if (modificationDate != null) {
            builder.and(cashier.modificationDate.loe(modificationDate));
        }

        Page<Cashier> result = cashierRepository.findAll(builder, pageable);

        return result.map(cashierMapper::asDTO);
    }

    @Override
    public CashierDTO getCashierById(Long id) {
        return cashierMapper.asDTO(cashierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cashier", "ID", id)));
    }
}