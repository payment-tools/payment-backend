package com.sn.onepay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record EnterpriseConfigurationCreateDTO(

        @Schema(name = "enterpriseId", description = "Identifier of the enterprise this configuration belongs to")
        @NotNull
        Long enterpriseId,

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
        Integer enterprisePercentage,

        @Schema(name = "employeePercentage", description = "Percentage of the employee")
        @NotNull(message = "Employee percentage can not be null")
        Integer employeePercentage
) {
}
