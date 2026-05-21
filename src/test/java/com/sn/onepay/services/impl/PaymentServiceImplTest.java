package com.sn.onepay.services.impl;

import com.sn.onepay.dto.CashierDTO;
import com.sn.onepay.dto.ClientDTO;
import com.sn.onepay.dto.EnterpriseConfigurationDTO;
import com.sn.onepay.dto.EnterpriseDTO;
import com.sn.onepay.dto.PartnershipDTO;
import com.sn.onepay.dto.PaymentDTO;
import com.sn.onepay.dto.SalesConfigurationsDTO;
import com.sn.onepay.dto.SalesDTO;
import com.sn.onepay.entity.Payment;
import com.sn.onepay.enumeration.Modules;
import com.sn.onepay.enumeration.StateStatus;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.PaymentMapper;
import com.sn.onepay.repository.PaymentRepository;
import com.sn.onepay.services.EnterpriseConfigurationService;
import com.sn.onepay.services.PartnershipService;
import com.sn.onepay.services.SalesConfigurationsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaymentServiceImplTest {

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    PaymentMapper paymentMapper;

    @Mock
    EnterpriseConfigurationService enterpriseConfigurationService;

    @Mock
    SalesConfigurationsService salesConfigurationsService;

    @Mock
    PartnershipService partnershipService;

    @InjectMocks
    PaymentServiceImpl paymentService;

    private PaymentDTO buildPaymentDTO(Modules module, Double amount) {
        var salesDTO = mock(SalesDTO.class);
        when(salesDTO.id()).thenReturn(1L);
        when(salesDTO.type()).thenReturn(module);

        var cashierDTO = mock(CashierDTO.class);
        when(cashierDTO.id()).thenReturn(1L);
        when(cashierDTO.sales()).thenReturn(salesDTO);

        var enterpriseDTO = mock(EnterpriseDTO.class);
        when(enterpriseDTO.id()).thenReturn(2L);

        var clientDTO = mock(ClientDTO.class);
        when(clientDTO.id()).thenReturn(3L);
        when(clientDTO.enterprise()).thenReturn(enterpriseDTO);

        var dto = mock(PaymentDTO.class);
        when(dto.cashier()).thenReturn(cashierDTO);
        when(dto.client()).thenReturn(clientDTO);
        when(dto.amount()).thenReturn(amount);
        return dto;
    }

    private SalesConfigurationsDTO buildSalesConfig(Double min, Double max) {
        var config = mock(SalesConfigurationsDTO.class);
        when(config.minAmount()).thenReturn(min);
        when(config.maxAmount()).thenReturn(max);
        return config;
    }

    private EnterpriseConfigurationDTO buildEnterpriseConfig(Double resto, Double market, Double gas, Double tel) {
        var config = mock(EnterpriseConfigurationDTO.class);
        when(config.maxAmountRestauration()).thenReturn(resto);
        when(config.maxAmountMarket()).thenReturn(market);
        when(config.maxAmountGasStation()).thenReturn(gas);
        when(config.maxAmountTelephony()).thenReturn(tel);
        return config;
    }

    private PartnershipDTO buildActivePartnership() {
        var p = mock(PartnershipDTO.class);
        when(p.status()).thenReturn(StateStatus.ACTIVE);
        return p;
    }

    @Test
    void createPayment_throwsWhenPartnershipIsNull() {
        var dto = buildPaymentDTO(Modules.RESTAURATION, 50.0);
        var salesConfig = buildSalesConfig(10.0, 100.0);
        var enterpriseConfig = buildEnterpriseConfig(500.0, 500.0, 500.0, 500.0);
        when(salesConfigurationsService.getSalesConfigurationsBySalesId(1L)).thenReturn(salesConfig);
        when(enterpriseConfigurationService.getEnterpriseConfigurationByEnterpriseId(2L)).thenReturn(enterpriseConfig);
        when(partnershipService.getPartnershipsBySalesIdAndEnterpriseId(1L, 2L)).thenReturn(null);

        assertThatThrownBy(() -> paymentService.createPayment(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("non autorisé");
    }

    @Test
    void createPayment_throwsWhenPartnershipInactive() {
        var dto = buildPaymentDTO(Modules.RESTAURATION, 50.0);
        var inactivePartnership = mock(PartnershipDTO.class);
        when(inactivePartnership.status()).thenReturn(StateStatus.INACTIVE);
        var salesConfig = buildSalesConfig(10.0, 100.0);
        var enterpriseConfig = buildEnterpriseConfig(500.0, 500.0, 500.0, 500.0);

        when(salesConfigurationsService.getSalesConfigurationsBySalesId(1L)).thenReturn(salesConfig);
        when(enterpriseConfigurationService.getEnterpriseConfigurationByEnterpriseId(2L)).thenReturn(enterpriseConfig);
        when(partnershipService.getPartnershipsBySalesIdAndEnterpriseId(1L, 2L)).thenReturn(inactivePartnership);

        assertThatThrownBy(() -> paymentService.createPayment(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("non autorisé");
    }

    @Test
    void createPayment_throwsWhenAmountBelowMin() {
        var dto = buildPaymentDTO(Modules.RESTAURATION, 5.0);
        var salesConfig = buildSalesConfig(10.0, 100.0);
        var enterpriseConfig = buildEnterpriseConfig(500.0, 500.0, 500.0, 500.0);
        var activePartnership = buildActivePartnership();
        when(salesConfigurationsService.getSalesConfigurationsBySalesId(1L)).thenReturn(salesConfig);
        when(enterpriseConfigurationService.getEnterpriseConfigurationByEnterpriseId(2L)).thenReturn(enterpriseConfig);
        when(partnershipService.getPartnershipsBySalesIdAndEnterpriseId(1L, 2L)).thenReturn(activePartnership);

        assertThatThrownBy(() -> paymentService.createPayment(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("non autorisé");
    }

    @Test
    void createPayment_restauration_happyPath() {
        var dto = buildPaymentDTO(Modules.RESTAURATION, 50.0);
        var entity = new Payment();
        var saved = new Payment();
        var resultDTO = mock(PaymentDTO.class);
        var salesConfig = buildSalesConfig(10.0, 100.0);
        var enterpriseConfig = buildEnterpriseConfig(500.0, 500.0, 500.0, 500.0);
        var activePartnership = buildActivePartnership();

        when(salesConfigurationsService.getSalesConfigurationsBySalesId(1L)).thenReturn(salesConfig);
        when(enterpriseConfigurationService.getEnterpriseConfigurationByEnterpriseId(2L)).thenReturn(enterpriseConfig);
        when(partnershipService.getPartnershipsBySalesIdAndEnterpriseId(1L, 2L)).thenReturn(activePartnership);
        when(paymentRepository.findAllPaymentsByClientIdAndModule(3L, "RESTAURATION")).thenReturn(0.0);
        when(paymentMapper.asEntity(dto)).thenReturn(entity);
        when(paymentRepository.save(entity)).thenReturn(saved);
        when(paymentMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = paymentService.createPayment(dto);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void createPayment_restauration_throwsWhenExceedsMax() {
        var dto = buildPaymentDTO(Modules.RESTAURATION, 50.0);
        var salesConfig = buildSalesConfig(10.0, 100.0);
        var enterpriseConfig = buildEnterpriseConfig(60.0, 500.0, 500.0, 500.0);
        var activePartnership = buildActivePartnership();

        when(salesConfigurationsService.getSalesConfigurationsBySalesId(1L)).thenReturn(salesConfig);
        when(enterpriseConfigurationService.getEnterpriseConfigurationByEnterpriseId(2L)).thenReturn(enterpriseConfig);
        when(partnershipService.getPartnershipsBySalesIdAndEnterpriseId(1L, 2L)).thenReturn(activePartnership);
        when(paymentRepository.findAllPaymentsByClientIdAndModule(3L, "RESTAURATION")).thenReturn(20.0);

        assertThatThrownBy(() -> paymentService.createPayment(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("Montant dépassé");
    }

    @Test
    void createPayment_market_happyPath() {
        var dto = buildPaymentDTO(Modules.MARKET, 30.0);
        var entity = new Payment();
        var saved = new Payment();
        var resultDTO = mock(PaymentDTO.class);
        var salesConfig = buildSalesConfig(10.0, 100.0);
        var enterpriseConfig = buildEnterpriseConfig(500.0, 500.0, 500.0, 500.0);
        var activePartnership = buildActivePartnership();

        when(salesConfigurationsService.getSalesConfigurationsBySalesId(1L)).thenReturn(salesConfig);
        when(enterpriseConfigurationService.getEnterpriseConfigurationByEnterpriseId(2L)).thenReturn(enterpriseConfig);
        when(partnershipService.getPartnershipsBySalesIdAndEnterpriseId(1L, 2L)).thenReturn(activePartnership);
        when(paymentRepository.findAllPaymentsByClientIdAndModule(3L, "MARKET")).thenReturn(0.0);
        when(paymentMapper.asEntity(dto)).thenReturn(entity);
        when(paymentRepository.save(entity)).thenReturn(saved);
        when(paymentMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = paymentService.createPayment(dto);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void createPayment_market_throwsWhenExceedsMax() {
        var dto = buildPaymentDTO(Modules.MARKET, 60.0);
        var salesConfig = buildSalesConfig(10.0, 100.0);
        var enterpriseConfig = buildEnterpriseConfig(500.0, 80.0, 500.0, 500.0);
        var activePartnership = buildActivePartnership();

        when(salesConfigurationsService.getSalesConfigurationsBySalesId(1L)).thenReturn(salesConfig);
        when(enterpriseConfigurationService.getEnterpriseConfigurationByEnterpriseId(2L)).thenReturn(enterpriseConfig);
        when(partnershipService.getPartnershipsBySalesIdAndEnterpriseId(1L, 2L)).thenReturn(activePartnership);
        when(paymentRepository.findAllPaymentsByClientIdAndModule(3L, "MARKET")).thenReturn(30.0);

        assertThatThrownBy(() -> paymentService.createPayment(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("Montant dépassé");
    }

    @Test
    void createPayment_gasStation_happyPath() {
        var dto = buildPaymentDTO(Modules.GAS_STATION, 40.0);
        var entity = new Payment();
        var saved = new Payment();
        var resultDTO = mock(PaymentDTO.class);
        var salesConfig = buildSalesConfig(10.0, 100.0);
        var enterpriseConfig = buildEnterpriseConfig(500.0, 500.0, 500.0, 500.0);
        var activePartnership = buildActivePartnership();

        when(salesConfigurationsService.getSalesConfigurationsBySalesId(1L)).thenReturn(salesConfig);
        when(enterpriseConfigurationService.getEnterpriseConfigurationByEnterpriseId(2L)).thenReturn(enterpriseConfig);
        when(partnershipService.getPartnershipsBySalesIdAndEnterpriseId(1L, 2L)).thenReturn(activePartnership);
        when(paymentRepository.findAllPaymentsByClientIdAndModule(3L, "GAS_STATION")).thenReturn(0.0);
        when(paymentMapper.asEntity(dto)).thenReturn(entity);
        when(paymentRepository.save(entity)).thenReturn(saved);
        when(paymentMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = paymentService.createPayment(dto);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void createPayment_gasStation_throwsWhenExceedsMax() {
        var dto = buildPaymentDTO(Modules.GAS_STATION, 80.0);
        var salesConfig = buildSalesConfig(10.0, 100.0);
        var enterpriseConfig = buildEnterpriseConfig(500.0, 500.0, 100.0, 500.0);
        var activePartnership = buildActivePartnership();

        when(salesConfigurationsService.getSalesConfigurationsBySalesId(1L)).thenReturn(salesConfig);
        when(enterpriseConfigurationService.getEnterpriseConfigurationByEnterpriseId(2L)).thenReturn(enterpriseConfig);
        when(partnershipService.getPartnershipsBySalesIdAndEnterpriseId(1L, 2L)).thenReturn(activePartnership);
        when(paymentRepository.findAllPaymentsByClientIdAndModule(3L, "GAS_STATION")).thenReturn(30.0);

        assertThatThrownBy(() -> paymentService.createPayment(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("Montant dépassé");
    }

    @Test
    void createPayment_telephony_happyPath() {
        var dto = buildPaymentDTO(Modules.TELEPHONY, 20.0);
        var entity = new Payment();
        var saved = new Payment();
        var resultDTO = mock(PaymentDTO.class);
        var salesConfig = buildSalesConfig(10.0, 100.0);
        var enterpriseConfig = buildEnterpriseConfig(500.0, 500.0, 500.0, 500.0);
        var activePartnership = buildActivePartnership();

        when(salesConfigurationsService.getSalesConfigurationsBySalesId(1L)).thenReturn(salesConfig);
        when(enterpriseConfigurationService.getEnterpriseConfigurationByEnterpriseId(2L)).thenReturn(enterpriseConfig);
        when(partnershipService.getPartnershipsBySalesIdAndEnterpriseId(1L, 2L)).thenReturn(activePartnership);
        when(paymentRepository.findAllPaymentsByClientIdAndModule(3L, "TELEPHONY")).thenReturn(0.0);
        when(paymentMapper.asEntity(dto)).thenReturn(entity);
        when(paymentRepository.save(entity)).thenReturn(saved);
        when(paymentMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = paymentService.createPayment(dto);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void createPayment_telephony_throwsWhenExceedsMax() {
        var dto = buildPaymentDTO(Modules.TELEPHONY, 60.0);
        var salesConfig = buildSalesConfig(10.0, 100.0);
        var enterpriseConfig = buildEnterpriseConfig(500.0, 500.0, 500.0, 70.0);
        var activePartnership = buildActivePartnership();

        when(salesConfigurationsService.getSalesConfigurationsBySalesId(1L)).thenReturn(salesConfig);
        when(enterpriseConfigurationService.getEnterpriseConfigurationByEnterpriseId(2L)).thenReturn(enterpriseConfig);
        when(partnershipService.getPartnershipsBySalesIdAndEnterpriseId(1L, 2L)).thenReturn(activePartnership);
        when(paymentRepository.findAllPaymentsByClientIdAndModule(3L, "TELEPHONY")).thenReturn(20.0);

        assertThatThrownBy(() -> paymentService.createPayment(dto))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessageContaining("Montant dépassé");
    }

    @Test
    void updatePayment_throwsWhenNotFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.updatePayment(mock(PaymentDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updatePayment_savesWhenFound() {
        var dto = mock(PaymentDTO.class);
        var existing = new Payment();
        var updated = new Payment();
        var resultDTO = mock(PaymentDTO.class);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(paymentMapper.asEntity(dto)).thenReturn(updated);
        when(paymentRepository.saveAndFlush(updated)).thenReturn(updated);
        when(paymentMapper.asDTO(updated)).thenReturn(resultDTO);

        var result = paymentService.updatePayment(dto, 1L);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void deletePayment_throwsWhenNotFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.deletePayment(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deletePayment_setsStatusToInactive() {
        var payment = new Payment();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.saveAndFlush(payment)).thenReturn(payment);

        paymentService.deletePayment(1L);

        assertThat(payment.getStatus()).isEqualTo(StateStatus.INACTIVE);
        verify(paymentRepository).saveAndFlush(payment);
    }

    @Test
    void getSumOfAllPaymentsByClientIdAndModule_returnsZeroWhenNull() {
        when(paymentRepository.findAllPaymentsByClientIdAndModule(1L, "RESTAURATION")).thenReturn(null);

        double result = paymentService.getSumOfAllPaymentsByClientIdAndModule(1L, Modules.RESTAURATION);
        assertThat(result).isEqualTo(0.0);
    }

    @Test
    void getSumOfAllPaymentsByClientIdAndModule_returnsValueWhenNotNull() {
        when(paymentRepository.findAllPaymentsByClientIdAndModule(1L, "RESTAURATION")).thenReturn(150.0);

        double result = paymentService.getSumOfAllPaymentsByClientIdAndModule(1L, Modules.RESTAURATION);
        assertThat(result).isEqualTo(150.0);
    }

    @Test
    void getSumOfAllPaymentsByClientId_returnsZeroWhenNull() {
        when(paymentRepository.findSumOfAllActivePaymentsByClientId(1L)).thenReturn(null);

        double result = paymentService.getSumOfAllPaymentsByClientId(1L);
        assertThat(result).isEqualTo(0.0);
    }

    @Test
    void getSumOfAllPaymentsByClientId_returnsValueWhenNotNull() {
        when(paymentRepository.findSumOfAllActivePaymentsByClientId(1L)).thenReturn(200.0);

        double result = paymentService.getSumOfAllPaymentsByClientId(1L);
        assertThat(result).isEqualTo(200.0);
    }

    @Test
    void getPaymentByFilters_withAllNullParams_returnsPage() {
        var page = new PageImpl<>(List.of(new Payment()));
        when(paymentRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(paymentMapper.asDTO(any(Payment.class))).thenReturn(mock(PaymentDTO.class));

        Page<PaymentDTO> result = paymentService.getPaymentByFilters(
                null, null, null, null, null, null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getPaymentByFilters_withAllParamsSet_returnsPage() {
        var page = new PageImpl<>(List.of(new Payment()));
        when(paymentRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(paymentMapper.asDTO(any(Payment.class))).thenReturn(mock(PaymentDTO.class));

        Page<PaymentDTO> result = paymentService.getPaymentByFilters(
                1L, "REF", 1L, 2L, 50.0, StateStatus.ACTIVE, Modules.RESTAURATION,
                LocalDateTime.now(), LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }
}