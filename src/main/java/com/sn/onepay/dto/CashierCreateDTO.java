package com.sn.onepay.dto;

import com.sn.onepay.enumeration.Roles;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CashierCreateDTO(

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

        @Schema(name = "salesId", description = "Identifier of the sales point this cashier belongs to")
        @NotNull
        Long salesId
) {
}
