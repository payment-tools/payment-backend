package com.sn.onepay.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record EmployeeGroupUpdateDTO(

        @Schema(name = "name", description = "EmployeeGroup Name")
        String name,

        @Schema(name = "clientIds", description = "Identifiers of the clients (employees) replacing the current group members")
        List<Long> clientIds,

        @Schema(name = "active", description = "Whether the EmployeeGroup is active")
        Boolean active
) {
}
