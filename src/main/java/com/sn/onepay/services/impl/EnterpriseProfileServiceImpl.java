package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.EnterpriseProfileCreateDTO;
import com.sn.onepay.dto.EnterpriseProfileDTO;
import com.sn.onepay.dto.EnterpriseProfileUpdateDTO;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.entity.EnterpriseProfile;
import com.sn.onepay.entity.QEnterpriseProfile;
import com.sn.onepay.enumeration.PlatformUserType;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.keycloak.AccountFields;
import com.sn.onepay.keycloak.KeycloakAccountService;
import com.sn.onepay.mapper.EnterpriseProfileMapper;
import com.sn.onepay.repository.EnterpriseProfileRepository;
import com.sn.onepay.repository.EnterpriseRepository;
import com.sn.onepay.services.EnterpriseProfileService;
import com.sn.onepay.utils.AccountUniquenessChecker;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class EnterpriseProfileServiceImpl implements EnterpriseProfileService {

    final AccountUniquenessChecker accountUniquenessChecker;
    final EnterpriseProfileRepository enterpriseProfileRepository;
    final EnterpriseRepository enterpriseRepository;
    final EnterpriseProfileMapper enterpriseProfileMapper;
    final KeycloakAccountService keycloakAccountService;

    @Override
    public EnterpriseProfileDTO createEnterpriseProfile(EnterpriseProfileCreateDTO enterpriseProfileCreateDTO) {

        accountUniquenessChecker.checkAvailable(PlatformUserType.ENTERPRISE_PROFILE, null, enterpriseProfileCreateDTO.username(), enterpriseProfileCreateDTO.email(), enterpriseProfileCreateDTO.phoneNumber());

        Enterprise enterprise = enterpriseRepository.findById(enterpriseProfileCreateDTO.enterpriseId())
                .orElseThrow(() -> new ResourceNotFoundException("Enterprise", "ID", enterpriseProfileCreateDTO.enterpriseId()));

        EnterpriseProfile profile = new EnterpriseProfile();
        profile.setFirstname(enterpriseProfileCreateDTO.firstname());
        profile.setLastname(enterpriseProfileCreateDTO.lastname());
        profile.setUsername(enterpriseProfileCreateDTO.username());
        profile.setEmail(enterpriseProfileCreateDTO.email());
        profile.setPhoneNumber(enterpriseProfileCreateDTO.phoneNumber());
        profile.setRole(KeycloakAccountService.requireRole(enterpriseProfileCreateDTO.role(), null, Set.of(Roles.ENTERPRISE_ADMIN, Roles.ENTERPRISE_FINANCE)));
        profile.setEnterprise(enterprise);
        profile.setActive(true);
        var savedEnterpriseProfile = enterpriseProfileRepository.save(profile);
        keycloakAccountService.createAccount(new AccountFields(profile.getUsername(), profile.getEmail(), profile.getFirstname(), profile.getLastname(), profile.getRole(), profile.isActive()));

        log.info("Created Enterprise Profile: {}", savedEnterpriseProfile);
        log.trace("Created Enterprise Profile with id: {}", savedEnterpriseProfile.getId());

        return enterpriseProfileMapper.asDTO(savedEnterpriseProfile);
    }

    @Override
    public EnterpriseProfileDTO updateEnterpriseProfile(EnterpriseProfileUpdateDTO enterpriseProfileUpdateDTO, Long enterpriseProfileId) {

        accountUniquenessChecker.checkAvailable(PlatformUserType.ENTERPRISE_PROFILE, enterpriseProfileId, enterpriseProfileUpdateDTO.username(), enterpriseProfileUpdateDTO.email(), enterpriseProfileUpdateDTO.phoneNumber());

        EnterpriseProfile existing = enterpriseProfileRepository.findById(enterpriseProfileId).orElseThrow( () -> new ResourceNotFoundException("Enterprise Profile", "ID", enterpriseProfileId));
        AccountFields before = new AccountFields(existing.getUsername(), existing.getEmail(), existing.getFirstname(), existing.getLastname(), existing.getRole(), existing.isActive());

        if (enterpriseProfileUpdateDTO.firstname() != null) existing.setFirstname(enterpriseProfileUpdateDTO.firstname());
        if (enterpriseProfileUpdateDTO.lastname() != null) existing.setLastname(enterpriseProfileUpdateDTO.lastname());
        if (enterpriseProfileUpdateDTO.username() != null) existing.setUsername(enterpriseProfileUpdateDTO.username());
        if (enterpriseProfileUpdateDTO.email() != null) existing.setEmail(enterpriseProfileUpdateDTO.email());
        if (enterpriseProfileUpdateDTO.phoneNumber() != null) existing.setPhoneNumber(enterpriseProfileUpdateDTO.phoneNumber());
        if (enterpriseProfileUpdateDTO.role() != null) existing.setRole(enterpriseProfileUpdateDTO.role());
        if (enterpriseProfileUpdateDTO.active() != null) existing.setActive(enterpriseProfileUpdateDTO.active());

        AccountFields after = new AccountFields(existing.getUsername(), existing.getEmail(), existing.getFirstname(), existing.getLastname(), existing.getRole(), existing.isActive());
        KeycloakAccountService.checkUpdatable(before, after);

        var updatedEnterpriseProfile = enterpriseProfileRepository.saveAndFlush(existing);
        keycloakAccountService.updateAccount(before, after);

        log.info("Updated Enterprise Profile: {}", updatedEnterpriseProfile);
        log.trace("Updated Enterprise Profile with id: {}", updatedEnterpriseProfile.getId());

        return enterpriseProfileMapper.asDTO(updatedEnterpriseProfile);
    }

    @Override
    public void deleteEnterpriseProfile(Long enterpriseProfileId) {

        EnterpriseProfile profile = enterpriseProfileRepository.findById(enterpriseProfileId).orElseThrow( () -> new ResourceNotFoundException("Enterprise Profile", "ID", enterpriseProfileId));
        profile.setActive(false);
        enterpriseProfileRepository.saveAndFlush(profile);
        keycloakAccountService.disableAccount(profile.getUsername());

        log.info("Deleted Enterprise Profile: {}", enterpriseProfileId);
        log.trace("Deleted Enterprise Profile with id: {}", enterpriseProfileId);
    }

    @Override
    public Page<EnterpriseProfileDTO> getEnterpriseProfilesByFilters(Long id, String ref, String firstname, String lastname, String username, String email, String phoneNumber, Roles role, Long enterpriseId, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable) {

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
        if (active != null) {
            builder.and(enterpriseProfile.active.eq(active));
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

    @Override
    public EnterpriseProfileDTO getMyProfile(String username) {

        EnterpriseProfile profile = enterpriseProfileRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Enterprise Profile", "username", username));

        return enterpriseProfileMapper.asDTO(profile);
    }

    @Override
    public EnterpriseProfileDTO getEnterpriseProfileById(Long id) {
        return enterpriseProfileMapper.asDTO(enterpriseProfileRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Enterprise Profile", "ID", id)));
    }
}