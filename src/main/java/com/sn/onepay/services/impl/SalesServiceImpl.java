package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.SalesDTO;
import com.sn.onepay.entity.QSales;
import com.sn.onepay.entity.Sales;
import com.sn.onepay.enumeration.Modules;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.SalesMapper;
import com.sn.onepay.repository.SalesRepository;
import com.sn.onepay.services.SalesService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class SalesServiceImpl implements SalesService {

    final SalesRepository salesRepository;
    final SalesMapper salesMapper;

    @Override
    public SalesDTO createSales(SalesDTO salesDTO) {

        Sales sales = salesMapper.asEntity(salesDTO);
        sales.setRef(UUID.randomUUID().toString());
        sales.setActive(true);

        var savedSales = salesRepository.save(sales);

        log.info("Sales saved: {}", savedSales);
        log.trace("Sales saved with id: {}", savedSales.getId());

        return salesMapper.asDTO(savedSales);

    }

    @Override
    public SalesDTO updateSales(SalesDTO salesDTO, Long salesId) {

        Sales existing = salesRepository.findById(salesId).orElseThrow(() -> new ResourceNotFoundException("Sales", "ID", salesId));

        if (salesDTO.ref() != null) existing.setRef(salesDTO.ref());
        if (salesDTO.name() != null) existing.setName(salesDTO.name());
        if (salesDTO.address() != null) existing.setAddress(salesDTO.address());
        if (salesDTO.type() != null) existing.setType(salesDTO.type());
        if (salesDTO.active() != null) existing.setActive(salesDTO.active());

        var updatedSales = salesRepository.saveAndFlush(existing);

        log.info("Sales updated: {}", updatedSales);
        log.debug("Sales updated with id: {}", updatedSales.getId());

        return salesMapper.asDTO(updatedSales);
    }

    @Override
    public void deleteSales(Long id) {

        Sales sales = salesRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sales", "ID", id));

        /*Implements logical deletion*/
        sales.setActive(false);
        salesRepository.saveAndFlush(sales);

        log.info("Sales deleted with id: {}", id);
        log.debug("Sales deleted with id: {}", id);

    }

    @Override
    public Page<SalesDTO> getSalesByFilters(Long id, String ref, String name, Modules type, String address, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable) {

        QSales sales = QSales.sales;
        BooleanBuilder builder = new BooleanBuilder();

        if(!Objects.isNull(id)) {
            builder.and(sales.id.eq(id));
        }
        if (ref != null && !ref.isEmpty()) {
            builder.and(sales.ref.containsIgnoreCase(ref));
        }
        if (name != null && !name.isEmpty()) {
            builder.and(sales.name.containsIgnoreCase(name));
        }
        if (type != null) {
            builder.and(sales.type.eq(type));
        }
        if (address != null && !address.isEmpty()) {
            builder.and(sales.address.containsIgnoreCase(address));
        }
        if (active != null) {
            builder.and(sales.active.eq(active));
        }
        if (creationDate != null) {
            builder.and(sales.creationDate.goe(creationDate));
        }
        if (modificationDate != null) {
            builder.and(sales.modificationDate.loe(modificationDate));
        }

        Page<Sales> result = salesRepository.findAll(builder, pageable);

        return result.map(salesMapper::asDTO);
    }
}
