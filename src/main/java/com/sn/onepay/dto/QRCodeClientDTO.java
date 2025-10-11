package com.sn.onepay.dto;

public record QRCodeClientDTO(
        Long clientId,
        Double maxAmountRestauration,
        Double maxAmountGasStation,
        Double maxAmountTelephony,
        Double maxAmountMarket
) {
}
