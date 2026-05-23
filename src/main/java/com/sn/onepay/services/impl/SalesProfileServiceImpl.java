package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.SalesProfileDTO;
import com.sn.onepay.entity.QSalesProfile;
import com.sn.onepay.entity.SalesProfile;
import com.sn.onepay.enumeration.Roles;
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

import java.time.LocalDateTime;

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

        SalesProfile profile = salesProfileMapper.asEntity(salesProfileDTO);
        profile.setActive(true);
        var savedSalesProfile = salesProfileRepository.save(profile);

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

        SalesProfile profile = salesProfileRepository.findById(salesProfileId).orElseThrow( () -> new ResourceNotFoundException("SalesProfile", "ID", salesProfileId));
        profile.setActive(false);
        salesProfileRepository.saveAndFlush(profile);

        log.info("Sales profile deleted with id: {}", salesProfileId);
        log.trace("Sales profile deleted with id: {}", salesProfileId);

    }

    @Override
    public Page<SalesProfileDTO> getSalesProfilesByFilters(Long id, String ref, String firstname, String lastname, String username, String email, String phoneNumber, Roles role, Long salesId, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable) {

        QSalesProfile salesProfile = QSalesProfile.salesProfile;
        BooleanBuilder builder = new BooleanBuilder();

        if (id != null) {
            builder.and(salesProfile.id.eq(id));
        }
        if (ref != null && !ref.isEmpty()) {
            builder.and(salesProfile.ref.containsIgnoreCase(ref));
        }
        if (firstname != null && !firstname.isEmpty()) {
            builder.and(salesProfile.firstname.containsIgnoreCase(firstname));
        }
        if (lastname != null && !lastname.isEmpty()) {
            builder.and(salesProfile.lastname.containsIgnoreCase(lastname));
        }
        if (username != null && !username.isEmpty()) {
            builder.and(salesProfile.username.containsIgnoreCase(username));
        }
        if (email != null && !email.isEmpty()) {
            builder.and(salesProfile.email.containsIgnoreCase(email));
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            builder.and(salesProfile.phoneNumber.containsIgnoreCase(phoneNumber));
        }
        if (role != null) {
            builder.and(salesProfile.role.eq(role));
        }
        if (salesId != null) {
            builder.and(salesProfile.sales.id.eq(salesId));
        }
        if (active != null) {
            builder.and(salesProfile.active.eq(active));
        }
        if (creationDate != null) {
            builder.and(salesProfile.creationDate.goe(creationDate));
        }
        if (modificationDate != null) {
            builder.and(salesProfile.modificationDate.loe(modificationDate));
        }

        Page<SalesProfile> result = salesProfileRepository.findAll(builder, pageable);
        return result.map(salesProfileMapper::asDTO);
    }
}