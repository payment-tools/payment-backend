package com.sn.onepay.dto;

import com.sn.onepay.entity.Enterprise;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EnterpriseConfigurationDTO(

        @Schema(name = "id", description = "Enterprise configuration identifier")
        Long id,

        @Schema(name = "enterprise", description = "Enterprise linked configuration")
        Enterprise enterprise,

        @Schema(name = "maxAmountRestauration", description = "Enterprise max amount for restauration")
        Double maxAmountRestauration,

        @Schema(name = "maxAmountMarket", description = "Enterprise max amount for market")
        Double maxAmountMarket,

        @Schema(name = "maxAmountGasStation", description = "Enterprise max amount for gas station")
        Double maxAmountGasStation,

        @Schema(name = "maxAmountTelephony", description = "Enterprise max amount for telephony")
        Double maxAmountTelephony,

        @Schema(name = "enterprisePercentage", description = "Percentage of the enterprise")
        @NotNull(message = "Enterprise percentage can not be null")
        int enterprisePercentage,

        @Schema(name = "", description = "Percentage of the employee")
        @NotNull(message = "Employee percentage can not be null")
        int employeePercentage,

        @Schema(name = "creationDate", description = "Enterprise configuration date of creation")
        LocalDateTime creationDate,

        @Schema(name = "modificationDate", description = "Enterprise configuration date of modification")
        LocalDateTime modificationDate
) {
}
