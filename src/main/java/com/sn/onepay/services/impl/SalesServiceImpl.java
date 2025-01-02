package com.sn.onepay.services.impl;

import com.sn.onepay.dto.SalesDTO;
import com.sn.onepay.exceptions.ResourceAlreadyExistException;
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

        var savedSales = salesRepository.save(salesMapper.asEntity(salesDTO));

        log.info("Sales saved: {}", savedSales);
        log.trace("Sales saved with id: {}", savedSales.getId());

        return salesMapper.asDTO(savedSales);

    }

    @Override
    public SalesDTO updateSales(SalesDTO salesDTO, Long salesId) {

        salesRepository.findById(salesId).orElseThrow(() -> new ResourceNotFoundException("Sales", "ID", salesId));

        var updatedSales = salesRepository.saveAndFlush(salesMapper.asEntity(salesDTO));

        log.info("Sales updated: {}", updatedSales);
        log.debug("Sales updated with id: {}", updatedSales.getId());

        return salesMapper.asDTO(updatedSales);
    }

    @Override
    public void deleteSales(Long id) {

        salesRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sales", "ID", id));

        salesRepository.deleteById(id);

        log.info("Sales deleted with id: {}", id);
        log.debug("Sales deleted with id: {}", id);

    }

    @Override
    public Page<SalesDTO> getSalesByFilter(SalesDTO salesDTO, Pageable pageable) {
        return null;
    }
}
