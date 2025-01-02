package com.sn.onepay.services.impl;

import com.sn.onepay.dto.SalesProfileDTO;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.SalesProfileMapper;
import com.sn.onepay.repository.SalesProfileRepository;
import com.sn.onepay.services.SalesProfileService;
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
public class SalesProfileServiceImpl implements SalesProfileService {

    final SalesProfileRepository salesProfileRepository;
    final SalesProfileMapper salesProfileMapper;

    @Override
    public SalesProfileDTO createSalesProfile(SalesProfileDTO salesProfileDTO) {

        var savedSalesProfile = salesProfileRepository.save(salesProfileMapper.asEntity(salesProfileDTO));

        log.info("Sales profile saved: {}", savedSalesProfile);
        log.trace("Sales profile saved with id: {}", savedSalesProfile.getId());

        return salesProfileMapper.asDTO(savedSalesProfile);
    }

    @Override
    public SalesProfileDTO updateSalesProfile(SalesProfileDTO salesProfileDTO, Long salesProfileId) {

        salesProfileRepository.findById(salesProfileId).orElseThrow( () -> new ResourceNotFoundException("SalesProfile", "ID", salesProfileId));

        var updatedSalesProfile = salesProfileRepository.save(salesProfileMapper.asEntity(salesProfileDTO));

        log.info("Sales profile updated: {}", updatedSalesProfile);
        log.trace("Sales profile updated with id: {}", updatedSalesProfile.getId());

        return salesProfileMapper.asDTO(updatedSalesProfile);
    }

    @Override
    public void deleteSalesProfile(Long salesProfileId) {

        salesProfileRepository.findById(salesProfileId).orElseThrow( () -> new ResourceNotFoundException("SalesProfile", "ID", salesProfileId));

        salesProfileRepository.deleteById(salesProfileId);

        log.info("Sales profile deleted with id: {}", salesProfileId);
        log.trace("Sales profile deleted with id: {}", salesProfileId);

    }

    @Override
    public Page<SalesProfileDTO> getSalesProfilesByFilter(SalesProfileDTO salesProfileDTO, Pageable pageable) {
        return null;
    }
}
