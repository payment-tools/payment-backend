package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.PlatformUserDTO;
import com.sn.onepay.entity.Cashier;
import com.sn.onepay.entity.Client;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.entity.EnterpriseProfile;
import com.sn.onepay.entity.QCashier;
import com.sn.onepay.entity.QClient;
import com.sn.onepay.entity.QEnterpriseProfile;
import com.sn.onepay.entity.QSalesProfile;
import com.sn.onepay.entity.Sales;
import com.sn.onepay.entity.SalesProfile;
import com.sn.onepay.enumeration.PlatformUserType;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.repository.CashierRepository;
import com.sn.onepay.repository.ClientRepository;
import com.sn.onepay.repository.EnterpriseProfileRepository;
import com.sn.onepay.repository.SalesProfileRepository;
import com.sn.onepay.services.PlatformUserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class PlatformUserServiceImpl implements PlatformUserService {

    final ClientRepository clientRepository;
    final CashierRepository cashierRepository;
    final EnterpriseProfileRepository enterpriseProfileRepository;
    final SalesProfileRepository salesProfileRepository;

    @Override
    public Page<PlatformUserDTO> getPlatformUsersByFilters(String search, Roles role, PlatformUserType userType, Boolean active, Pageable pageable) {

        List<PlatformUserDTO> merged = new ArrayList<>();

        if (userType == null || userType == PlatformUserType.CLIENT) {
            for (Client client : clientRepository.findAll(buildClientPredicate(search, role, active))) {
                merged.add(mapClient(client));
            }
        }
        if (userType == null || userType == PlatformUserType.CASHIER) {
            for (Cashier cashier : cashierRepository.findAll(buildCashierPredicate(search, role, active))) {
                merged.add(mapCashier(cashier));
            }
        }
        if (userType == null || userType == PlatformUserType.ENTERPRISE_PROFILE) {
            for (EnterpriseProfile profile : enterpriseProfileRepository.findAll(buildEnterpriseProfilePredicate(search, role, active))) {
                merged.add(mapEnterpriseProfile(profile));
            }
        }
        if (userType == null || userType == PlatformUserType.SALES_PROFILE) {
            for (SalesProfile profile : salesProfileRepository.findAll(buildSalesProfilePredicate(search, role, active))) {
                merged.add(mapSalesProfile(profile));
            }
        }

        merged.sort(Comparator.comparing(PlatformUserDTO::creationDate, Comparator.nullsLast(Comparator.reverseOrder())));

        log.info("Platform users merged: {} total", merged.size());

        return paginate(merged, pageable);
    }

    private Page<PlatformUserDTO> paginate(List<PlatformUserDTO> merged, Pageable pageable) {

        int total = merged.size();

        if (!pageable.isPaged()) {
            return new PageImpl<>(merged, pageable, total);
        }

        int start = (int) pageable.getOffset();
        if (start >= total) {
            return new PageImpl<>(List.of(), pageable, total);
        }

        int end = Math.min(start + pageable.getPageSize(), total);
        return new PageImpl<>(merged.subList(start, end), pageable, total);
    }

    private BooleanBuilder buildClientPredicate(String search, Roles role, Boolean active) {
        QClient client = QClient.client;
        BooleanBuilder builder = new BooleanBuilder();
        if (search != null && !search.isEmpty()) {
            builder.and(client.firstname.containsIgnoreCase(search)
                    .or(client.lastname.containsIgnoreCase(search))
                    .or(client.username.containsIgnoreCase(search))
                    .or(client.email.containsIgnoreCase(search)));
        }
        if (role != null) {
            builder.and(client.role.eq(role));
        }
        if (active != null) {
            builder.and(client.active.eq(active));
        }
        return builder;
    }

    private BooleanBuilder buildCashierPredicate(String search, Roles role, Boolean active) {
        QCashier cashier = QCashier.cashier;
        BooleanBuilder builder = new BooleanBuilder();
        if (search != null && !search.isEmpty()) {
            builder.and(cashier.firstname.containsIgnoreCase(search)
                    .or(cashier.lastname.containsIgnoreCase(search))
                    .or(cashier.username.containsIgnoreCase(search))
                    .or(cashier.email.containsIgnoreCase(search)));
        }
        if (role != null) {
            builder.and(cashier.role.eq(role));
        }
        if (active != null) {
            builder.and(cashier.active.eq(active));
        }
        return builder;
    }

    private BooleanBuilder buildEnterpriseProfilePredicate(String search, Roles role, Boolean active) {
        QEnterpriseProfile profile = QEnterpriseProfile.enterpriseProfile;
        BooleanBuilder builder = new BooleanBuilder();
        if (search != null && !search.isEmpty()) {
            builder.and(profile.firstname.containsIgnoreCase(search)
                    .or(profile.lastname.containsIgnoreCase(search))
                    .or(profile.username.containsIgnoreCase(search))
                    .or(profile.email.containsIgnoreCase(search)));
        }
        if (role != null) {
            builder.and(profile.role.eq(role));
        }
        if (active != null) {
            builder.and(profile.active.eq(active));
        }
        return builder;
    }

    private BooleanBuilder buildSalesProfilePredicate(String search, Roles role, Boolean active) {
        QSalesProfile profile = QSalesProfile.salesProfile;
        BooleanBuilder builder = new BooleanBuilder();
        if (search != null && !search.isEmpty()) {
            builder.and(profile.firstname.containsIgnoreCase(search)
                    .or(profile.lastname.containsIgnoreCase(search))
                    .or(profile.username.containsIgnoreCase(search))
                    .or(profile.email.containsIgnoreCase(search)));
        }
        if (role != null) {
            builder.and(profile.role.eq(role));
        }
        if (active != null) {
            builder.and(profile.active.eq(active));
        }
        return builder;
    }

    private PlatformUserDTO mapClient(Client client) {
        Enterprise enterprise = client.getEnterprise();
        Long organizationId = enterprise != null ? enterprise.getId() : null;
        String organizationName = enterprise != null ? enterprise.getName() : null;
        return new PlatformUserDTO(PlatformUserType.CLIENT, client.getId(), client.getRef(), client.getFirstname(), client.getLastname(),
                client.getUsername(), client.getEmail(), client.getPhoneNumber(), client.getRole(),
                organizationId, organizationName, "ENTERPRISE", client.isActive(), client.getCreationDate(), client.getModificationDate());
    }

    private PlatformUserDTO mapCashier(Cashier cashier) {
        Sales sales = cashier.getSales();
        Long organizationId = sales != null ? sales.getId() : null;
        String organizationName = sales != null ? sales.getName() : null;
        return new PlatformUserDTO(PlatformUserType.CASHIER, cashier.getId(), cashier.getRef(), cashier.getFirstname(), cashier.getLastname(),
                cashier.getUsername(), cashier.getEmail(), cashier.getPhoneNumber(), cashier.getRole(),
                organizationId, organizationName, "SALES", cashier.isActive(), cashier.getCreationDate(), cashier.getModificationDate());
    }

    private PlatformUserDTO mapEnterpriseProfile(EnterpriseProfile profile) {
        Enterprise enterprise = profile.getEnterprise();
        Long organizationId = enterprise != null ? enterprise.getId() : null;
        String organizationName = enterprise != null ? enterprise.getName() : null;
        return new PlatformUserDTO(PlatformUserType.ENTERPRISE_PROFILE, profile.getId(), profile.getRef(), profile.getFirstname(), profile.getLastname(),
                profile.getUsername(), profile.getEmail(), profile.getPhoneNumber(), profile.getRole(),
                organizationId, organizationName, "ENTERPRISE", profile.isActive(), profile.getCreationDate(), profile.getModificationDate());
    }

    private PlatformUserDTO mapSalesProfile(SalesProfile profile) {
        Sales sales = profile.getSales();
        Long organizationId = sales != null ? sales.getId() : null;
        String organizationName = sales != null ? sales.getName() : null;
        return new PlatformUserDTO(PlatformUserType.SALES_PROFILE, profile.getId(), profile.getRef(), profile.getFirstname(), profile.getLastname(),
                profile.getUsername(), profile.getEmail(), profile.getPhoneNumber(), profile.getRole(),
                organizationId, organizationName, "SALES", profile.isActive(), profile.getCreationDate(), profile.getModificationDate());
    }

    @Override
    public PlatformUserDTO getPlatformUserByTypeAndId(PlatformUserType userType, Long id) {

        if (userType == PlatformUserType.CLIENT) {
            return mapClient(clientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Client", "ID", id)));
        }
        if (userType == PlatformUserType.CASHIER) {
            return mapCashier(cashierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cashier", "ID", id)));
        }
        if (userType == PlatformUserType.ENTERPRISE_PROFILE) {
            return mapEnterpriseProfile(enterpriseProfileRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("EnterpriseProfile", "ID", id)));
        }
        return mapSalesProfile(salesProfileRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("SalesProfile", "ID", id)));
    }
}
