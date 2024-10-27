package com.sn.onepay.dto;

import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.enumeration.StateStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ClientDTO(

        @Schema(name = "id", description = "Client Identifier")
        Long id,

        @Schema(name = "ref", description = "Client Reference")
        String ref,

        @Schema(name = "firstname", description = "Client firstname")
        String firstname,

        @Schema(name = "lastname", description = "Client lastname")
        String lastname,

        @Schema(name = "username", description = "Client username")
        @NotNull
        String username,

        @Schema(name = "email", description = "Client email")
        @NotNull
        @Email
        String email,

        @Schema(name = "phoneNumber", description = "Client Phone Number")
        String phoneNumber,

        @Schema(name = "role", description = "Client Role")
        Roles role,

        @Schema(name = "sales", description = "Enterprise of the Client")
        Enterprise enterprise,

        @Schema(name = "status", description = "Status of the Client")
        StateStatus status,

        @Schema(name = "creationDate", description = "Client date of creation")
        LocalDateTime creationDate,

        @Schema(name = "modificationDate", description = "Client date of modification")
        LocalDateTime modificationDate
        
) {
}
