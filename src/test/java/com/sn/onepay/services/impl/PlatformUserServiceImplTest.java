package com.sn.onepay.services.impl;

import com.querydsl.core.types.Predicate;
import com.sn.onepay.dto.PlatformUserDTO;
import com.sn.onepay.entity.Cashier;
import com.sn.onepay.entity.Client;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.entity.EnterpriseProfile;
import com.sn.onepay.entity.Sales;
import com.sn.onepay.entity.SalesProfile;
import com.sn.onepay.enumeration.PlatformUserType;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.repository.CashierRepository;
import com.sn.onepay.repository.ClientRepository;
import com.sn.onepay.repository.EnterpriseProfileRepository;
import com.sn.onepay.repository.SalesProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatformUserServiceImplTest {

    @Mock
    ClientRepository clientRepository;

    @Mock
    CashierRepository cashierRepository;

    @Mock
    EnterpriseProfileRepository enterpriseProfileRepository;

    @Mock
    SalesProfileRepository salesProfileRepository;

    @InjectMocks
    PlatformUserServiceImpl platformUserService;

    private Client client(Long id, boolean withEnterprise, LocalDateTime creationDate) {
        var client = new Client();
        client.setId(id);
        client.setRef("REF-C" + id);
        client.setFirstname("Jean");
        client.setLastname("Dupont");
        client.setUsername("jdupont" + id);
        client.setEmail("jean" + id + "@mail.com");
        client.setPhoneNumber("77000000" + id);
        client.setRole(Roles.CLIENT);
        client.setActive(true);
        client.setCreationDate(creationDate);
        if (withEnterprise) {
            var enterprise = new Enterprise();
            enterprise.setId(1L);
            enterprise.setName("Acme");
            client.setEnterprise(enterprise);
        }
        return client;
    }

    private Cashier cashier(Long id, boolean withSales, LocalDateTime creationDate) {
        var cashier = new Cashier();
        cashier.setId(id);
        cashier.setRef("REF-CA" + id);
        cashier.setFirstname("Awa");
        cashier.setLastname("Ndiaye");
        cashier.setUsername("andiaye" + id);
        cashier.setEmail("awa" + id + "@mail.com");
        cashier.setRole(Roles.CASHIER);
        cashier.setActive(true);
        cashier.setCreationDate(creationDate);
        if (withSales) {
            var sales = new Sales();
            sales.setId(2L);
            sales.setName("Shop");
            cashier.setSales(sales);
        }
        return cashier;
    }

    private EnterpriseProfile enterpriseProfile(Long id, boolean withEnterprise, LocalDateTime creationDate) {
        var profile = new EnterpriseProfile();
        profile.setId(id);
        profile.setRef("REF-EP" + id);
        profile.setFirstname("Moussa");
        profile.setLastname("Diop");
        profile.setUsername("mdiop" + id);
        profile.setEmail("moussa" + id + "@mail.com");
        profile.setRole(Roles.ENTERPRISE_ADMIN);
        profile.setActive(true);
        profile.setCreationDate(creationDate);
        if (withEnterprise) {
            var enterprise = new Enterprise();
            enterprise.setId(1L);
            enterprise.setName("Acme");
            profile.setEnterprise(enterprise);
        }
        return profile;
    }

    private SalesProfile salesProfile(Long id, boolean withSales, LocalDateTime creationDate) {
        var profile = new SalesProfile();
        profile.setId(id);
        profile.setRef("REF-SP" + id);
        profile.setFirstname("Fatou");
        profile.setLastname("Sow");
        profile.setUsername("fsow" + id);
        profile.setEmail("fatou" + id + "@mail.com");
        profile.setRole(Roles.SALES_ADMIN);
        profile.setActive(true);
        profile.setCreationDate(creationDate);
        if (withSales) {
            var sales = new Sales();
            sales.setId(2L);
            sales.setName("Shop");
            profile.setSales(sales);
        }
        return profile;
    }

    @Test
    void getPlatformUsersByFilters_withAllNullParams_mergesAllFourSourcesOrderedByCreationDateDesc() {
        var now = LocalDateTime.now();
        when(clientRepository.findAll(any(Predicate.class))).thenReturn(List.of(client(1L, true, now.minusDays(3))));
        when(cashierRepository.findAll(any(Predicate.class))).thenReturn(List.of(cashier(1L, false, now.minusDays(1))));
        when(enterpriseProfileRepository.findAll(any(Predicate.class))).thenReturn(List.of(enterpriseProfile(1L, true, now.minusDays(2))));
        when(salesProfileRepository.findAll(any(Predicate.class))).thenReturn(List.of(salesProfile(1L, false, now)));

        Page<PlatformUserDTO> result = platformUserService.getPlatformUsersByFilters(null, null, null, null, Pageable.unpaged());

        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getContent()).extracting(PlatformUserDTO::userType)
                .containsExactly(PlatformUserType.SALES_PROFILE, PlatformUserType.CASHIER, PlatformUserType.ENTERPRISE_PROFILE, PlatformUserType.CLIENT);

        var clientDTO = result.getContent().stream().filter(u -> u.userType() == PlatformUserType.CLIENT).findFirst().orElseThrow();
        assertThat(clientDTO.organizationId()).isEqualTo(1L);
        assertThat(clientDTO.organizationName()).isEqualTo("Acme");
        assertThat(clientDTO.organizationType()).isEqualTo("ENTERPRISE");

        var cashierDTO = result.getContent().stream().filter(u -> u.userType() == PlatformUserType.CASHIER).findFirst().orElseThrow();
        assertThat(cashierDTO.organizationId()).isNull();
        assertThat(cashierDTO.organizationName()).isNull();
        assertThat(cashierDTO.organizationType()).isEqualTo("SALES");
    }

    @Test
    void getPlatformUsersByFilters_mapsNullOrganizationForEnterpriseProfileWithoutEnterprise() {
        when(clientRepository.findAll(any(Predicate.class))).thenReturn(List.of());
        when(cashierRepository.findAll(any(Predicate.class))).thenReturn(List.of());
        when(enterpriseProfileRepository.findAll(any(Predicate.class))).thenReturn(List.of(enterpriseProfile(1L, false, LocalDateTime.now())));
        when(salesProfileRepository.findAll(any(Predicate.class))).thenReturn(List.of());

        Page<PlatformUserDTO> result = platformUserService.getPlatformUsersByFilters(null, null, null, null, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).organizationId()).isNull();
        assertThat(result.getContent().get(0).organizationName()).isNull();
    }

    @Test
    void getPlatformUsersByFilters_withAllFiltersSet_returnsPage() {
        when(clientRepository.findAll(any(Predicate.class))).thenReturn(List.of());
        when(cashierRepository.findAll(any(Predicate.class))).thenReturn(List.of());
        when(enterpriseProfileRepository.findAll(any(Predicate.class))).thenReturn(List.of());
        when(salesProfileRepository.findAll(any(Predicate.class))).thenReturn(List.of());

        Page<PlatformUserDTO> result = platformUserService.getPlatformUsersByFilters("jean", Roles.CLIENT, null, true, Pageable.unpaged());

        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void getPlatformUsersByFilters_withEmptySearch_returnsPage() {
        when(clientRepository.findAll(any(Predicate.class))).thenReturn(List.of());
        when(cashierRepository.findAll(any(Predicate.class))).thenReturn(List.of());
        when(enterpriseProfileRepository.findAll(any(Predicate.class))).thenReturn(List.of());
        when(salesProfileRepository.findAll(any(Predicate.class))).thenReturn(List.of());

        Page<PlatformUserDTO> result = platformUserService.getPlatformUsersByFilters("", null, null, null, Pageable.unpaged());

        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void getPlatformUsersByFilters_withUserTypeClient_onlyQueriesClientRepository() {
        when(clientRepository.findAll(any(Predicate.class))).thenReturn(List.of());

        platformUserService.getPlatformUsersByFilters(null, null, PlatformUserType.CLIENT, null, Pageable.unpaged());

        verify(clientRepository).findAll(any(Predicate.class));
        verifyNoInteractions(cashierRepository, enterpriseProfileRepository, salesProfileRepository);
    }

    @Test
    void getPlatformUsersByFilters_withUserTypeCashier_onlyQueriesCashierRepository() {
        when(cashierRepository.findAll(any(Predicate.class))).thenReturn(List.of());

        platformUserService.getPlatformUsersByFilters(null, null, PlatformUserType.CASHIER, null, Pageable.unpaged());

        verify(cashierRepository).findAll(any(Predicate.class));
        verifyNoInteractions(clientRepository, enterpriseProfileRepository, salesProfileRepository);
    }

    @Test
    void getPlatformUsersByFilters_withUserTypeEnterpriseProfile_onlyQueriesEnterpriseProfileRepository() {
        when(enterpriseProfileRepository.findAll(any(Predicate.class))).thenReturn(List.of());

        platformUserService.getPlatformUsersByFilters(null, null, PlatformUserType.ENTERPRISE_PROFILE, null, Pageable.unpaged());

        verify(enterpriseProfileRepository).findAll(any(Predicate.class));
        verifyNoInteractions(clientRepository, cashierRepository, salesProfileRepository);
    }

    @Test
    void getPlatformUsersByFilters_withUserTypeSalesProfile_onlyQueriesSalesProfileRepository() {
        when(salesProfileRepository.findAll(any(Predicate.class))).thenReturn(List.of());

        platformUserService.getPlatformUsersByFilters(null, null, PlatformUserType.SALES_PROFILE, null, Pageable.unpaged());

        verify(salesProfileRepository).findAll(any(Predicate.class));
        verifyNoInteractions(clientRepository, cashierRepository, enterpriseProfileRepository);
    }

    @Test
    void getPlatformUsersByFilters_pagination_firstPage() {
        var now = LocalDateTime.now();
        when(clientRepository.findAll(any(Predicate.class))).thenReturn(List.of(
                client(1L, false, now.minusDays(1)), client(2L, false, now.minusDays(2)), client(3L, false, now.minusDays(3))));
        when(cashierRepository.findAll(any(Predicate.class))).thenReturn(List.of());
        when(enterpriseProfileRepository.findAll(any(Predicate.class))).thenReturn(List.of());
        when(salesProfileRepository.findAll(any(Predicate.class))).thenReturn(List.of());

        Page<PlatformUserDTO> result = platformUserService.getPlatformUsersByFilters(null, null, null, null, PageRequest.of(0, 2));

        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void getPlatformUsersByFilters_pagination_lastPartialPage() {
        var now = LocalDateTime.now();
        when(clientRepository.findAll(any(Predicate.class))).thenReturn(List.of(
                client(1L, false, now.minusDays(1)), client(2L, false, now.minusDays(2)), client(3L, false, now.minusDays(3))));
        when(cashierRepository.findAll(any(Predicate.class))).thenReturn(List.of());
        when(enterpriseProfileRepository.findAll(any(Predicate.class))).thenReturn(List.of());
        when(salesProfileRepository.findAll(any(Predicate.class))).thenReturn(List.of());

        Page<PlatformUserDTO> result = platformUserService.getPlatformUsersByFilters(null, null, null, null, PageRequest.of(1, 2));

        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getPlatformUsersByFilters_pagination_pageBeyondTotal_returnsEmpty() {
        when(clientRepository.findAll(any(Predicate.class))).thenReturn(List.of(client(1L, false, LocalDateTime.now())));
        when(cashierRepository.findAll(any(Predicate.class))).thenReturn(List.of());
        when(enterpriseProfileRepository.findAll(any(Predicate.class))).thenReturn(List.of());
        when(salesProfileRepository.findAll(any(Predicate.class))).thenReturn(List.of());

        Page<PlatformUserDTO> result = platformUserService.getPlatformUsersByFilters(null, null, null, null, PageRequest.of(5, 2));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void getPlatformUserByTypeAndId_client_returnsMappedDTOWhenFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client(1L, true, LocalDateTime.now())));

        var result = platformUserService.getPlatformUserByTypeAndId(PlatformUserType.CLIENT, 1L);

        assertThat(result.userType()).isEqualTo(PlatformUserType.CLIENT);
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getPlatformUserByTypeAndId_client_throwsWhenNotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> platformUserService.getPlatformUserByTypeAndId(PlatformUserType.CLIENT, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getPlatformUserByTypeAndId_cashier_returnsMappedDTOWhenFound() {
        when(cashierRepository.findById(1L)).thenReturn(Optional.of(cashier(1L, true, LocalDateTime.now())));

        var result = platformUserService.getPlatformUserByTypeAndId(PlatformUserType.CASHIER, 1L);

        assertThat(result.userType()).isEqualTo(PlatformUserType.CASHIER);
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getPlatformUserByTypeAndId_cashier_throwsWhenNotFound() {
        when(cashierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> platformUserService.getPlatformUserByTypeAndId(PlatformUserType.CASHIER, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getPlatformUserByTypeAndId_enterpriseProfile_returnsMappedDTOWhenFound() {
        when(enterpriseProfileRepository.findById(1L)).thenReturn(Optional.of(enterpriseProfile(1L, true, LocalDateTime.now())));

        var result = platformUserService.getPlatformUserByTypeAndId(PlatformUserType.ENTERPRISE_PROFILE, 1L);

        assertThat(result.userType()).isEqualTo(PlatformUserType.ENTERPRISE_PROFILE);
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getPlatformUserByTypeAndId_enterpriseProfile_throwsWhenNotFound() {
        when(enterpriseProfileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> platformUserService.getPlatformUserByTypeAndId(PlatformUserType.ENTERPRISE_PROFILE, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getPlatformUserByTypeAndId_salesProfile_returnsMappedDTOWhenFound() {
        when(salesProfileRepository.findById(1L)).thenReturn(Optional.of(salesProfile(1L, true, LocalDateTime.now())));

        var result = platformUserService.getPlatformUserByTypeAndId(PlatformUserType.SALES_PROFILE, 1L);

        assertThat(result.userType()).isEqualTo(PlatformUserType.SALES_PROFILE);
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getPlatformUserByTypeAndId_salesProfile_throwsWhenNotFound() {
        when(salesProfileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> platformUserService.getPlatformUserByTypeAndId(PlatformUserType.SALES_PROFILE, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
