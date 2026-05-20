package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.EnterpriseProfileDTO;
import com.sn.onepay.entity.EnterpriseProfile;
import com.sn.onepay.entity.QEnterpriseProfile;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.enumeration.StateStatus;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.EnterpriseProfileMapper;
import com.sn.onepay.repository.EnterpriseProfileRepository;
import com.sn.onepay.services.EnterpriseProfileService;
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
public class EnterpriseProfileServiceImpl implements EnterpriseProfileService {

    final EnterpriseProfileRepository enterpriseProfileRepository;
    final EnterpriseProfileMapper enterpriseProfileMapper;

    @Override
    public EnterpriseProfileDTO createEnterpriseProfile(EnterpriseProfileDTO enterpriseProfileDTO) {

        var savedEnterpriseProfile = enterpriseProfileRepository.save(enterpriseProfileMapper.asEntity(enterpriseProfileDTO));

        log.info("Created Enterprise Profile: {}", savedEnterpriseProfile);
        log.trace("Created Enterprise Profile with id: {}", savedEnterpriseProfile.getId());

        return enterpriseProfileMapper.asDTO(savedEnterpriseProfile);
    }

    @Override
    public EnterpriseProfileDTO updateEnterpriseProfile(EnterpriseProfileDTO enterpriseProfileDTO, Long enterpriseProfileId) {

        enterpriseProfileRepository.findById(enterpriseProfileId).orElseThrow( () -> new ResourceNotFoundException("Enterprise Profile", "ID", enterpriseProfileId));

        var updatedEnterpriseProfile = enterpriseProfileRepository.save(enterpriseProfileMapper.asEntity(enterpriseProfileDTO));

        log.info("Updated Enterprise Profile: {}", updatedEnterpriseProfile);
        log.trace("Updated Enterprise Profile with id: {}", updatedEnterpriseProfile.getId());

        return enterpriseProfileMapper.asDTO(updatedEnterpriseProfile);
    }

    @Override
    public void deleteEnterpriseProfile(Long enterpriseProfileId) {

        enterpriseProfileRepository.findById(enterpriseProfileId).orElseThrow( () -> new ResourceNotFoundException("Enterprise Profile", "ID", enterpriseProfileId));

        enterpriseProfileRepository.deleteById(enterpriseProfileId);

        log.info("Deleted Enterprise Profile: {}", enterpriseProfileId);
        log.trace("Deleted Enterprise Profile with id: {}", enterpriseProfileId);


    }

    @Override
    public Page<EnterpriseProfileDTO> getEnterpriseProfilesByFilters(Long id, String ref, String firstname, String lastname, String username, String email, String phoneNumber, Roles role, Long enterpriseId, StateStatus status, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable) {

        QEnterpriseProfile enterpriseProfile = QEnterpriseProfile.enterpriseProfile;
        BooleanBuilder builder = new BooleanBuilder();

        if (id != null) {
            builder.and(enterpriseProfile.id.eq(id));
        }
        if (ref != null && !ref.isEmpty()) {
            builder.and(enterpriseProfile.ref.containsIgnoreCase(ref));
        }
        if (firstname != null && !firstname.isEmpty()) {
            builder.and(enterpriseProfile.firstname.containsIgnoreCase(firstname));
        }
        if (lastname != null && !lastname.isEmpty()) {
            builder.and(enterpriseProfile.lastname.containsIgnoreCase(lastname));
        }
        if (username != null && !username.isEmpty()) {
            builder.and(enterpriseProfile.username.containsIgnoreCase(username));
        }
        if (email != null && !email.isEmpty()) {
            builder.and(enterpriseProfile.email.containsIgnoreCase(email));
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            builder.and(enterpriseProfile.phoneNumber.containsIgnoreCase(phoneNumber));
        }
        if (role != null) {
            builder.and(enterpriseProfile.role.eq(role));
        }
        if (enterpriseId != null) {
            builder.and(enterpriseProfile.enterprise.id.eq(enterpriseId));
        }
        if (status != null) {
            builder.and(enterpriseProfile.status.eq(status));
        }
        if (creationDate != null) {
            builder.and(enterpriseProfile.creationDate.goe(creationDate));
        }
        if (modificationDate != null) {
            builder.and(enterpriseProfile.modificationDate.loe(modificationDate));
        }

        Page<EnterpriseProfile> result = enterpriseProfileRepository.findAll(builder, pageable);
        return result.map(enterpriseProfileMapper::asDTO);
    }
}
