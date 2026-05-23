package com.sn.onepay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PartnershipDTO(

        @Schema(name = "id", description = "Partnership identifier")
        Long id,

        @Schema(name = "ref", description = "Partnership reference")
        String ref,

        @Schema(name = "sales", description = "Sales of the partnership")
        @NotNull(message = "Sales can not be null")
        SalesDTO sales,

        @Schema(name = "enterprise", description = "Enterprise of the partnership")
        @NotNull(message = "Enterprise can not be null")
        EnterpriseDTO enterprise,

        @Schema(name = "active", description = "Whether the partnership is active")
        @NotNull(message = "Active can not be null")
        Boolean active,

        @Schema(name = "creationDate", description = "Partnership date of creation")
        LocalDateTime creationDate,

        @Schema(name = "modificationDate", description = "Partnership profile date of modification")
        LocalDateTime modificationDate

) {
}
