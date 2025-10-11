package com.sn.onepay.controller;

import com.sn.onepay.dto.QRCodeCashierDTO;
import com.sn.onepay.dto.QRCodeClientDTO;
import com.sn.onepay.services.QRCodeService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping(value = "/v1/onepay/qrcode")
@RequiredArgsConstructor
public class QRCodeController {

    final QRCodeService qrCodeService;

    @GetMapping(value = "/client")
    @ResponseStatus(HttpStatus.OK)
    public QRCodeClientDTO getClientQRCode(
            @Parameter(description = "") @RequestParam(required = false) Long clientId) {
        return qrCodeService.getClientQrCode(clientId);
    }

    @GetMapping(value = "/cashier")
    @ResponseStatus(HttpStatus.OK)
    public QRCodeCashierDTO getCashierQRCode(
            @Parameter(description = "") @RequestParam(required = false) Long cashierId) {
        return qrCodeService.getCashierQrCode(cashierId);
    }
}
