package com.sn.onepay.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record EnterpriseConfigurationUpdateDTO(

        @Schema(name = "maxAmountRestauration", description = "Enterprise max amount for restauration")
        Double maxAmountRestauration,

        @Schema(name = "maxAmountMarket", description = "Enterprise max amount for market")
        Double maxAmountMarket,

        @Schema(name = "maxAmountGasStation", description = "Enterprise max amount for gas station")
        Double maxAmountGasStation,

        @Schema(name = "maxAmountTelephony", description = "Enterprise max amount for telephony")
        Double maxAmountTelephony,

        @Schema(name = "enterprisePercentage", description = "Percentage of the enterprise")
        Integer enterprisePercentage,

        @Schema(name = "employeePercentage", description = "Percentage of the employee")
        Integer employeePercentage,

        @Schema(name = "active", description = "Whether the enterprise configuration is active")
        Boolean active
) {
}
