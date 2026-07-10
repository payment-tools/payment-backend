package com.sn.onepay.services;

import com.sn.onepay.dto.QRCodeCashierDTO;
import com.sn.onepay.dto.QRCodeClientDTO;

public interface QRCodeService {
    QRCodeCashierDTO getCashierQrCode(Long cashierId);
    QRCodeClientDTO getClientQrCode(Long clientId);
    void consumeClientQrCode(String qrCodeRef, Long clientId);
}
