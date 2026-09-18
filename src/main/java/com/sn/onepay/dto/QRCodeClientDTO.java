package com.sn.onepay.dto;

import java.time.LocalDateTime;

public record QRCodeClientDTO(
        String ref,
        Long clientId,
        Boolean used,
        LocalDateTime expirationDate,
        Double maxAmountRestauration,
        Double maxAmountGasStation,
        Double maxAmountTelephony,
        Double maxAmountMarket
) {
}
