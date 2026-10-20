package com.sn.onepay.dto;

import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.utils.PhoneNumbers;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

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
        @NotBlank
        @Pattern(regexp = PhoneNumbers.SENEGAL_PATTERN, message = PhoneNumbers.FORMAT_MESSAGE)
        String phoneNumber,

        @Schema(name = "role", description = "Cashier Role")
        Roles role,

        @Schema(name = "salesId", description = "Identifier of the sales point this cashier belongs to")
        @NotNull
        Long salesId
) {
}
