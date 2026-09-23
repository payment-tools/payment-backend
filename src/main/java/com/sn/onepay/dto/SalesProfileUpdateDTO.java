package com.sn.onepay.dto;

import com.sn.onepay.enumeration.Roles;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

public record SalesProfileUpdateDTO(

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

        @Schema(name = "active", description = "Whether the profile is active")
        Boolean active
) {
}
