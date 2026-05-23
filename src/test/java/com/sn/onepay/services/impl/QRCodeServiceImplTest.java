package com.sn.onepay.services.impl;

import com.sn.onepay.entity.Cashier;
import com.sn.onepay.entity.Client;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.entity.EnterpriseConfiguration;
import com.sn.onepay.entity.Sales;
import com.sn.onepay.entity.SalesConfigurations;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.repository.CashierRepository;
import com.sn.onepay.repository.ClientRepository;
import com.sn.onepay.repository.EnterpriseConfigurationRepository;
import com.sn.onepay.repository.SalesConfigurationsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QRCodeServiceImplTest {

    @Mock
    CashierRepository cashierRepository;

    @Mock
    SalesConfigurationsRepository salesConfigurationsRepository;

    @Mock
    ClientRepository clientRepository;

    @Mock
    EnterpriseConfigurationRepository enterpriseConfigurationRepository;

    @InjectMocks
    QRCodeServiceImpl qrCodeService;

    @Test
    void getCashierQrCode_throwsWhenCashierNotFound() {
        when(cashierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> qrCodeService.getCashierQrCode(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getCashierQrCode_throwsWhenConfigurationIsNull() {
        var cashier = mock(Cashier.class);
        var sales = mock(Sales.class);
        when(cashier.getSales()).thenReturn(sales);
        when(cashierRepository.findById(1L)).thenReturn(Optional.of(cashier));
        when(salesConfigurationsRepository.findBySales(sales)).thenReturn(null);

        assertThatThrownBy(() -> qrCodeService.getCashierQrCode(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getCashierQrCode_happyPath() {
        var cashier = mock(Cashier.class);
        var sales = mock(Sales.class);
        when(sales.getId()).thenReturn(1L);
        when(cashier.getSales()).thenReturn(sales);
        when(cashierRepository.findById(1L)).thenReturn(Optional.of(cashier));

        var config = mock(SalesConfigurations.class);
        when(config.getMaxAmount()).thenReturn(100.0);
        when(config.getMinAmount()).thenReturn(10.0);
        when(salesConfigurationsRepository.findBySales(sales)).thenReturn(config);

        var result = qrCodeService.getCashierQrCode(1L);

        assertThat(result).isNotNull();
        assertThat(result.salesId()).isEqualTo(1L);
        assertThat(result.maxAmount()).isEqualTo(100.0);
        assertThat(result.minAmount()).isEqualTo(10.0);
    }

    @Test
    void getClientQrCode_throwsWhenClientNotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> qrCodeService.getClientQrCode(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getClientQrCode_throwsWhenConfigurationIsNull() {
        var client = mock(Client.class);
        var enterprise = mock(Enterprise.class);
        when(client.getEnterprise()).thenReturn(enterprise);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(enterpriseConfigurationRepository.findByEnterprise(enterprise)).thenReturn(null);

        assertThatThrownBy(() -> qrCodeService.getClientQrCode(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getClientQrCode_happyPath() {
        var client = mock(Client.class);
        var enterprise = mock(Enterprise.class);
        when(client.getEnterprise()).thenReturn(enterprise);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        var config = mock(EnterpriseConfiguration.class);
        when(config.getMaxAmountRestauration()).thenReturn(500.0);
        when(config.getMaxAmountGasStation()).thenReturn(200.0);
        when(config.getMaxAmountTelephony()).thenReturn(100.0);
        when(config.getMaxAmountMarket()).thenReturn(300.0);
        when(enterpriseConfigurationRepository.findByEnterprise(enterprise)).thenReturn(config);

        var result = qrCodeService.getClientQrCode(1L);

        assertThat(result).isNotNull();
        assertThat(result.clientId()).isEqualTo(1L);
        assertThat(result.maxAmountRestauration()).isEqualTo(500.0);
        assertThat(result.maxAmountMarket()).isEqualTo(300.0);
    }
}