package com.sn.onepay.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SalesConfigurationsUpdateDTO(

        @Schema(name = "minAmount", description = "Sales min amount for cashier")
        Double minAmount,

        @Schema(name = "maxAmount", description = "Sales max amount for cashier")
        Double maxAmount,

        @Schema(name = "active", description = "Whether the sales configuration is active")
        Boolean active
) {
}
