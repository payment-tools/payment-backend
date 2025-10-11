package com.sn.onepay.services.impl;

import com.sn.onepay.dto.QRCodeCashierDTO;
import com.sn.onepay.dto.QRCodeClientDTO;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.repository.CashierRepository;
import com.sn.onepay.repository.ClientRepository;
import com.sn.onepay.repository.EnterpriseConfigurationRepository;
import com.sn.onepay.repository.SalesConfigurationsRepository;
import com.sn.onepay.services.QRCodeService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class QRCodeServiceImpl implements QRCodeService {

    final CashierRepository cashierRepository;
    final SalesConfigurationsRepository salesConfigurationsRepository;
    final ClientRepository clientRepository;
    final EnterpriseConfigurationRepository enterpriseConfigurationRepository;

    @Override
    public QRCodeCashierDTO getCashierQrCode(Long cashierId) {
        var cashier = cashierRepository.findById(cashierId).orElseThrow(() -> new ResourceNotFoundException("Cashier ID", "ID", cashierId));
        var configuration = salesConfigurationsRepository.findBySales(cashier.getSales());
        if(Objects.isNull(configuration))
            throw new ResourceNotFoundException("Configuration", "configId", null);

        return new QRCodeCashierDTO(cashier.getSales().getId(), configuration.getMaxAmount(), configuration.getMinAmount());

    }

    @Override
    public QRCodeClientDTO getClientQrCode(Long clientId) {
        var client = clientRepository.findById(clientId).orElseThrow(() -> new ResourceNotFoundException("Client ID", "ID", clientId));
        var configuration = enterpriseConfigurationRepository.findByEnterprise(client.getEnterprise());
        if(Objects.isNull(configuration))
            throw new ResourceNotFoundException("Configuration", "configId", null);

        return new QRCodeClientDTO(clientId, configuration.getMaxAmountRestauration(), configuration.getMaxAmountGasStation(), configuration.getMaxAmountTelephony(), configuration.getMaxAmountMarket());
    }
}
