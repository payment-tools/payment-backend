package com.sn.onepay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SalesConfigurationsCreateDTO(

        @Schema(name = "salesId", description = "Identifier of the sales point this configuration belongs to")
        @NotNull
        Long salesId,

        @Schema(name = "minAmount", description = "Sales min amount for cashier")
        Double minAmount,

        @Schema(name = "maxAmount", description = "Sales max amount for cashier")
        Double maxAmount
) {
}
