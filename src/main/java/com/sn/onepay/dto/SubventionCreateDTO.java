package com.sn.onepay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SubventionCreateDTO(

        @Schema(name = "employeePercent", description = "Employee contribution percentage")
        @NotNull
        Double employeePercent,

        @Schema(name = "employerPercent", description = "Employer contribution percentage")
        @NotNull
        Double employerPercent,

        @Schema(name = "partnershipId", description = "Identifier of the partnership linked to this subvention")
        @NotNull
        Long partnershipId,

        @Schema(name = "employeeGroupId", description = "Identifier of the employee group this subvention applies to")
        @NotNull
        Long employeeGroupId
) {
}
