package com.sn.onepay.dto;

import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.entity.Sales;
import com.sn.onepay.enumeration.StateStatus;
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
        Sales sales,

        @Schema(name = "enterprise", description = "Enterprise of the partnership")
        @NotNull(message = "Enterprise can not be null")
        Enterprise enterprise,

        @Schema(name = "status", description = "Status of the partnership")
        @NotNull(message = "Status can not be null")
        StateStatus status,

        @Schema(name = "creationDate", description = "Partnership date of creation")
        LocalDateTime creationDate,

        @Schema(name = "modificationDate", description = "Partnership profile date of modification")
        LocalDateTime modificationDate

) {
}
