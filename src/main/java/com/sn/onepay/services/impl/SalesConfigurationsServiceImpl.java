package com.sn.onepay.services.impl;

import com.sn.onepay.dto.SalesConfigurationsDTO;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.SalesConfigurationsMapper;
import com.sn.onepay.repository.SalesConfigurationsRepository;
import com.sn.onepay.services.SalesConfigurationsService;
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
public class SalesConfigurationsServiceImpl implements SalesConfigurationsService {

    final SalesConfigurationsRepository salesConfigurationsRepository;
    final SalesConfigurationsMapper salesConfigurationsMapper;

    @Override
    public SalesConfigurationsDTO createSalesConfigurations(SalesConfigurationsDTO salesConfigurationsDTO) {

        var savedSalesConfigurations = salesConfigurationsRepository.save(salesConfigurationsMapper.asEntity(salesConfigurationsDTO));

        log.info("Sales configurations saved: {}", savedSalesConfigurations);
        log.trace("Sales configurations saved with id: {}", savedSalesConfigurations.getId());

        return salesConfigurationsMapper.asDTO(savedSalesConfigurations);

    }

    @Override
    public SalesConfigurationsDTO updateSalesConfigurations(SalesConfigurationsDTO salesConfigurationsDTO, Long salesConfigurationsId) {

        salesConfigurationsRepository.findById(salesConfigurationsId).orElseThrow(() -> new ResourceNotFoundException("Sales configurations", "ID", salesConfigurationsId));

        var updatedSalesConfigurations = salesConfigurationsMapper.asEntity(salesConfigurationsDTO);

        log.info("Sales configurations updated: {}", updatedSalesConfigurations);
        log.trace("Sales configurations updated with id: {}", updatedSalesConfigurations.getId());

        return salesConfigurationsMapper.asDTO(updatedSalesConfigurations);
    }

    @Override
    public void deleteSalesConfigurations(Long salesConfigurationsId) {

        salesConfigurationsRepository.findById(salesConfigurationsId).orElseThrow(() -> new ResourceNotFoundException("Sales configurations", "ID", salesConfigurationsId));

        salesConfigurationsRepository.deleteById(salesConfigurationsId);

        log.info("Sales configurations deleted with id: {}", salesConfigurationsId);
        log.trace("Sales configurations deleted with id: {}", salesConfigurationsId);

    }

    @Override
    public Page<SalesConfigurationsDTO> getSalesConfigurationssByFilter(SalesConfigurationsDTO salesConfigurationsDTO, Pageable pageable) {
        return null;
    }
}
