package com.sn.onepay.dto;

import com.sn.onepay.enumeration.StateStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record SubventionDTO(

        @Schema(name = "id", description = "Subvention Identifier")
        Long id,

        @Schema(name = "ref", description = "Subvention Reference")
        String ref,

        @Schema(name = "employeePercent", description = "Employee contribution percentage")
        @NotNull
        Double employeePercent,

        @Schema(name = "employerPercent", description = "Employer contribution percentage")
        @NotNull
        Double employerPercent,

        @Schema(name = "partnership", description = "Partnership linked to this subvention")
        @NotNull
        PartnershipDTO partnership,

        @Schema(name = "employeeGroup", description = "Employee group this subvention applies to")
        @NotNull
        EmployeeGroupDTO employeeGroup,

        @Schema(name = "status", description = "Status of the Subvention")
        StateStatus status,

        @Schema(name = "creationDate", description = "Subvention date of creation")
        LocalDateTime creationDate,

        @Schema(name = "modificationDate", description = "Subvention date of modification")
        LocalDateTime modificationDate
) {
}