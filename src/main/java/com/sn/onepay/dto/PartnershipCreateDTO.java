package com.sn.onepay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PartnershipCreateDTO(

        @Schema(name = "salesId", description = "Identifier of the sales point")
        @NotNull(message = "Sales id can not be null")
        Long salesId,

        @Schema(name = "enterpriseId", description = "Identifier of the enterprise")
        @NotNull(message = "Enterprise id can not be null")
        Long enterpriseId
) {
}
