package com.sn.onepay.dto;

import com.sn.onepay.enumeration.Roles;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

public record ClientUpdateDTO(

        @Schema(name = "firstname", description = "Client firstname")
        String firstname,

        @Schema(name = "lastname", description = "Client lastname")
        String lastname,

        @Schema(name = "username", description = "Client username")
        String username,

        @Schema(name = "email", description = "Client email")
        @Email
        String email,

        @Schema(name = "phoneNumber", description = "Client Phone Number")
        String phoneNumber,

        @Schema(name = "role", description = "Client Role")
        Roles role,

        @Schema(name = "active", description = "Whether the Client is active")
        Boolean active
) {
}
