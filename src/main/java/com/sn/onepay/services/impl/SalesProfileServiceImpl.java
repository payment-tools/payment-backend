package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.SalesProfileCreateDTO;
import com.sn.onepay.dto.SalesProfileDTO;
import com.sn.onepay.dto.SalesProfileUpdateDTO;
import com.sn.onepay.entity.QSalesProfile;
import com.sn.onepay.entity.Sales;
import com.sn.onepay.entity.SalesProfile;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.SalesProfileMapper;
import com.sn.onepay.repository.SalesProfileRepository;
import com.sn.onepay.repository.SalesRepository;
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
    final SalesRepository salesRepository;
    final SalesProfileMapper salesProfileMapper;

    @Override
    public SalesProfileDTO createSalesProfile(SalesProfileCreateDTO salesProfileCreateDTO) {

        Sales sales = salesRepository.findById(salesProfileCreateDTO.salesId())
                .orElseThrow(() -> new ResourceNotFoundException("Sales", "ID", salesProfileCreateDTO.salesId()));

        SalesProfile profile = new SalesProfile();
        profile.setFirstname(salesProfileCreateDTO.firstname());
        profile.setLastname(salesProfileCreateDTO.lastname());
        profile.setUsername(salesProfileCreateDTO.username());
        profile.setEmail(salesProfileCreateDTO.email());
        profile.setPhoneNumber(salesProfileCreateDTO.phoneNumber());
        profile.setRole(salesProfileCreateDTO.role());
        profile.setSales(sales);
        profile.setActive(true);
        var savedSalesProfile = salesProfileRepository.save(profile);

        log.info("Sales profile saved: {}", savedSalesProfile);
        log.trace("Sales profile saved with id: {}", savedSalesProfile.getId());

        return salesProfileMapper.asDTO(savedSalesProfile);
    }

    @Override
    public SalesProfileDTO updateSalesProfile(SalesProfileUpdateDTO salesProfileUpdateDTO, Long salesProfileId) {

        SalesProfile existing = salesProfileRepository.findById(salesProfileId).orElseThrow( () -> new ResourceNotFoundException("SalesProfile", "ID", salesProfileId));

        if (salesProfileUpdateDTO.firstname() != null) existing.setFirstname(salesProfileUpdateDTO.firstname());
        if (salesProfileUpdateDTO.lastname() != null) existing.setLastname(salesProfileUpdateDTO.lastname());
        if (salesProfileUpdateDTO.username() != null) existing.setUsername(salesProfileUpdateDTO.username());
        if (salesProfileUpdateDTO.email() != null) existing.setEmail(salesProfileUpdateDTO.email());
        if (salesProfileUpdateDTO.phoneNumber() != null) existing.setPhoneNumber(salesProfileUpdateDTO.phoneNumber());
        if (salesProfileUpdateDTO.role() != null) existing.setRole(salesProfileUpdateDTO.role());
        if (salesProfileUpdateDTO.active() != null) existing.setActive(salesProfileUpdateDTO.active());

        var updatedSalesProfile = salesProfileRepository.saveAndFlush(existing);

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

    @Override
    public SalesProfileDTO getMyProfile(String username) {

        SalesProfile profile = salesProfileRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("SalesProfile", "username", username));

        return salesProfileMapper.asDTO(profile);
    }

    @Override
    public SalesProfileDTO getSalesProfileById(Long id) {
        return salesProfileMapper.asDTO(salesProfileRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("SalesProfile", "ID", id)));
    }
}