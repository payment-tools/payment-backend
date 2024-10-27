package com.sn.onepay.dto;

import com.sn.onepay.entity.Sales;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record SalesConfigurationsDTO(

        @Schema(name = "id", description = "Sales configuration identifier")
        Long id,

        @Schema(name = "sales", description = "Sales linked configuration")
        Sales sales,

        @Schema(name = "minAmount", description = "Sales min amount for cashier")
        Double minAmount,

        @Schema(name = "maxAmount", description = "Sales max amount for cashier")
        Double maxAmount,

        @Schema(name = "creationDate", description = "Sales configuration date of creation")
        LocalDateTime creationDate,

        @Schema(name = "modificationDate", description = "Sales configuration date of modification")
        LocalDateTime modificationDate
) {
}
