package com.sn.onepay.dto;

import com.sn.onepay.enumeration.StateStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record EmployeeGroupDTO(

        @Schema(name = "id", description = "EmployeeGroup Identifier")
        Long id,

        @Schema(name = "ref", description = "EmployeeGroup Reference")
        String ref,

        @Schema(name = "name", description = "EmployeeGroup Name")
        @NotNull
        String name,

        @Schema(name = "enterprise", description = "Enterprise owning this group")
        @NotNull
        EnterpriseDTO enterprise,

        @Schema(name = "clients", description = "List of clients (employees) in this group")
        List<ClientDTO> clients,

        @Schema(name = "status", description = "Status of the EmployeeGroup")
        StateStatus status,

        @Schema(name = "creationDate", description = "EmployeeGroup date of creation")
        LocalDateTime creationDate,

        @Schema(name = "modificationDate", description = "EmployeeGroup date of modification")
        LocalDateTime modificationDate
) {
}