package com.sn.onepay.services.impl;

import com.sn.onepay.dto.QRCodeCashierDTO;
import com.sn.onepay.dto.QRCodeClientDTO;
import com.sn.onepay.entity.QRCodeClient;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.repository.CashierRepository;
import com.sn.onepay.repository.ClientRepository;
import com.sn.onepay.repository.EnterpriseConfigurationRepository;
import com.sn.onepay.repository.QRCodeClientRepository;
import com.sn.onepay.repository.SalesConfigurationsRepository;
import com.sn.onepay.services.QRCodeService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

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
    final QRCodeClientRepository qrCodeClientRepository;

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

        var qrCode = new QRCodeClient();
        qrCode.setRef(UUID.randomUUID().toString());
        qrCode.setClient(client);
        qrCode.setUsed(false);
        qrCode.setActive(true);
        qrCode = qrCodeClientRepository.save(qrCode);

        log.info("QRCode client saved: {}", qrCode);
        log.trace("QRCode client saved with id: {}", qrCode.getId());

        return new QRCodeClientDTO(qrCode.getRef(), clientId, qrCode.isUsed(), configuration.getMaxAmountRestauration(), configuration.getMaxAmountGasStation(), configuration.getMaxAmountTelephony(), configuration.getMaxAmountMarket());
    }

    @Override
    public void consumeClientQrCode(String qrCodeRef, Long clientId) {
        var qrCode = qrCodeClientRepository.findByRef(qrCodeRef).orElseThrow(() -> new ResourceNotFoundException("QRCode client", "ref", qrCodeRef));

        if (!qrCode.isActive() || !qrCode.getClient().getId().equals(clientId))
            throw new ObjectValidationException("QR code invalide");

        if (qrCode.isUsed())
            throw new ObjectValidationException("QR code déjà utilisé");

        qrCode.setUsed(true);
        qrCodeClientRepository.saveAndFlush(qrCode);

        log.info("QRCode client consumed: {}", qrCodeRef);
        log.trace("QRCode client consumed with id: {}", qrCode.getId());
    }
}
