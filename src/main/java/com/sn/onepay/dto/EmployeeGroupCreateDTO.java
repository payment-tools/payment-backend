package com.sn.onepay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record EmployeeGroupCreateDTO(

        @Schema(name = "name", description = "EmployeeGroup Name")
        @NotNull
        String name,

        @Schema(name = "enterpriseId", description = "Identifier of the enterprise owning this group")
        @NotNull
        Long enterpriseId,

        @Schema(name = "clientIds", description = "Identifiers of the clients (employees) in this group")
        List<Long> clientIds
) {
}
