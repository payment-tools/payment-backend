package com.sn.onepay.dto;

import com.sn.onepay.enumeration.Roles;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record EnterpriseProfileCreateDTO(

        @Schema(name = "firstname", description = "User's firstname")
        String firstname,

        @Schema(name = "lastname", description = "User's lastname")
        String lastname,

        @Schema(name = "username", description = "User's username")
        @NotNull
        String username,

        @Schema(name = "email", description = "User's email")
        @Email
        String email,

        @Schema(name = "phoneNumber", description = "User's phone number")
        String phoneNumber,

        @Schema(name = "role", description = "User's role")
        Roles role,

        @Schema(name = "enterpriseId", description = "Identifier of the enterprise this profile belongs to")
        @NotNull
        Long enterpriseId
) {
}
