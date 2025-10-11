package com.sn.onepay.dto;

public record QRCodeCashierDTO (
        Long salesId,
        Double maxAmount,
        Double minAmount) { }
