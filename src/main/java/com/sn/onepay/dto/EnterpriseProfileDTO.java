package com.sn.onepay.dto;

import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.enumeration.StateStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

import java.time.LocalDateTime;

public record EnterpriseProfileDTO(

        @Schema(name = "id", description = "Enterprise profile identifier")
        Long id,

        @Schema(name = "ref", description = "Enterprise profile reference")
        String ref,

        @Schema(name = "firstname", description = "User's firstname")
        String firstname,

        @Schema(name = "lastname", description = "User's lastname")
        String lastname,

        @Schema(name = "username", description = "User's username")
        String username,

        @Schema(name = "email", description = "User's email")
        @Email
        String email,

        @Schema(name = "phoneNumber", description = "User's phone number")
        String phoneNumber,

        @Schema(name = "role", description = "User's role")
        Roles role,

        @Schema(name = "enterprise", description = "User's enterprise")
        Enterprise enterprise,

        @Schema(name = "status", description = "User's status")
        StateStatus status,

        @Schema(name = "creationDate", description = "Enterprise profile date of creation")
        LocalDateTime creationDate,

        @Schema(name = "modificationDate", description = "Enterprise profile date of modification")
        LocalDateTime modificationDate
) {
}
