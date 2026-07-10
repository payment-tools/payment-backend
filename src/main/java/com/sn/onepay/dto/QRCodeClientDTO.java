package com.sn.onepay.dto;

public record QRCodeClientDTO(
        String ref,
        Long clientId,
        Boolean used,
        Double maxAmountRestauration,
        Double maxAmountGasStation,
        Double maxAmountTelephony,
        Double maxAmountMarket
) {
}
