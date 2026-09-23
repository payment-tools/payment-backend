package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.SalesConfigurationsDTO;
import com.sn.onepay.entity.QSalesConfigurations;
import com.sn.onepay.entity.SalesConfigurations;
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

import java.time.LocalDateTime;

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

        SalesConfigurations salesConfigurations = salesConfigurationsMapper.asEntity(salesConfigurationsDTO);
        salesConfigurations.setActive(true);

        var savedSalesConfigurations = salesConfigurationsRepository.save(salesConfigurations);

        log.info("Sales configurations saved: {}", savedSalesConfigurations);
        log.trace("Sales configurations saved with id: {}", savedSalesConfigurations.getId());

        return salesConfigurationsMapper.asDTO(savedSalesConfigurations);

    }

    @Override
    public SalesConfigurationsDTO updateSalesConfigurations(SalesConfigurationsDTO salesConfigurationsDTO, Long salesConfigurationsId) {

        SalesConfigurations existing = salesConfigurationsRepository.findById(salesConfigurationsId).orElseThrow(() -> new ResourceNotFoundException("Sales configurations", "ID", salesConfigurationsId));

        if (salesConfigurationsDTO.sales() != null) existing.setSales(salesConfigurationsDTO.sales());
        if (salesConfigurationsDTO.minAmount() != null) existing.setMinAmount(salesConfigurationsDTO.minAmount());
        if (salesConfigurationsDTO.maxAmount() != null) existing.setMaxAmount(salesConfigurationsDTO.maxAmount());
        if (salesConfigurationsDTO.active() != null) existing.setActive(salesConfigurationsDTO.active());

        var updatedSalesConfigurations = salesConfigurationsRepository.saveAndFlush(existing);

        log.info("Sales configurations updated: {}", updatedSalesConfigurations);
        log.trace("Sales configurations updated with id: {}", updatedSalesConfigurations.getId());

        return salesConfigurationsMapper.asDTO(updatedSalesConfigurations);
    }

    @Override
    public void deleteSalesConfigurations(Long salesConfigurationsId) {

        SalesConfigurations salesConfigurations = salesConfigurationsRepository.findById(salesConfigurationsId).orElseThrow(() -> new ResourceNotFoundException("Sales configurations", "ID", salesConfigurationsId));

        /*Implements logical deletion*/
        salesConfigurations.setActive(false);
        salesConfigurationsRepository.saveAndFlush(salesConfigurations);

        log.info("Sales configurations deleted with id: {}", salesConfigurationsId);
        log.trace("Sales configurations deleted with id: {}", salesConfigurationsId);

    }

    @Override
    public Page<SalesConfigurationsDTO> getSalesConfigurationsByFilters(Long id, Long salesId, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable) {

        QSalesConfigurations configurations = QSalesConfigurations.salesConfigurations;
        BooleanBuilder builder = new BooleanBuilder();

        if (id != null) {
            builder.and(configurations.id.eq(id));
        }
        if (salesId != null) {
            builder.and(configurations.sales.id.eq(salesId));
        }
        if (active != null) {
            builder.and(configurations.active.eq(active));
        }
        if (creationDate != null) {
            builder.and(configurations.creationDate.goe(creationDate));
        }
        if (modificationDate != null) {
            builder.and(configurations.modificationDate.loe(modificationDate));
        }

        Page<SalesConfigurations> result = salesConfigurationsRepository.findAll(builder, pageable);

        return result.map(salesConfigurationsMapper::asDTO);
    }

    @Override
    public SalesConfigurationsDTO getSalesConfigurationsBySalesId(Long salesId) {
        return salesConfigurationsRepository.getSalesConfigurationsBySalesId(salesId) ;
    }

    @Override
    public SalesConfigurationsDTO getSalesConfigurationsById(Long id) {
        return salesConfigurationsMapper.asDTO(salesConfigurationsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sales configurations", "ID", id)));
    }
}
