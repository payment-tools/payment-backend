package com.sn.onepay.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SubventionUpdateDTO(

        @Schema(name = "employeePercent", description = "Employee contribution percentage (must be provided together with employerPercent)")
        Double employeePercent,

        @Schema(name = "employerPercent", description = "Employer contribution percentage (must be provided together with employeePercent)")
        Double employerPercent,

        @Schema(name = "active", description = "Whether the Subvention is active")
        Boolean active
) {
}
