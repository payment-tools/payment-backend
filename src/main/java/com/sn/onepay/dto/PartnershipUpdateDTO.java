package com.sn.onepay.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PartnershipUpdateDTO(

        @Schema(name = "salesId", description = "Identifier of the sales point")
        Long salesId,

        @Schema(name = "enterpriseId", description = "Identifier of the enterprise")
        Long enterpriseId,

        @Schema(name = "active", description = "Whether the partnership is active")
        Boolean active
) {
}
