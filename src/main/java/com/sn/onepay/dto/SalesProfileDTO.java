package com.sn.onepay.dto;

import com.sn.onepay.entity.Sales;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.enumeration.StateStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

import java.time.LocalDateTime;

public record SalesProfileDTO(

        @Schema(name = "id", description = "Sales profile identifier")
        Long id,

        @Schema(name = "ref", description = "Sales profile reference")
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

        @Schema(name = "sales", description = "User's sales")
        Sales sales,

        @Schema(name = "status", description = "User's status")
        StateStatus status,

        @Schema(name = "creationDate", description = "Sales profile date of creation")
        LocalDateTime creationDate,

        @Schema(name = "modificationDate", description = "Sales profile date of modification")
        LocalDateTime modificationDate

) {
}
