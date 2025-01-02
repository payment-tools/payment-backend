package com.sn.onepay.dto;

import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.enumeration.StateStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CashierDTO(

        @Schema(name = "id", description = "Cashier Identifier")
        Long id,

        @Schema(name = "ref", description = "Cashier Reference")
        String ref,

        @Schema(name = "firstname", description = "Cashier firstname")
        String firstname,

        @Schema(name = "lastname", description = "Cashier lastname")
        String lastname,

        @Schema(name = "username", description = "Cashier username")
        @NotNull
        String username,

        @Schema(name = "email", description = "Cashier email")
        @NotNull
        @Email
        String email,

        @Schema(name = "phoneNumber", description = "Cashier Phone Number")
        String phoneNumber,

        @Schema(name = "role", description = "Cashier Role")
        Roles role,

        @Schema(name = "sales", description = "Sales of the cashier")
        SalesDTO sales,

        @Schema(name = "status", description = "Status of the cashier")
        StateStatus status,

        @Schema(name = "creationDate", description = "Cashier date of creation")
        LocalDateTime creationDate,

        @Schema(name = "modificationDate", description = "Cashier date of modification")
        LocalDateTime modificationDate
) {
}
