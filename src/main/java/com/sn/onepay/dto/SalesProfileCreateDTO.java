package com.sn.onepay.dto;

import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.utils.PhoneNumbers;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SalesProfileCreateDTO(

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
        @NotBlank
        @Pattern(regexp = PhoneNumbers.SENEGAL_PATTERN, message = PhoneNumbers.FORMAT_MESSAGE)
        String phoneNumber,

        @Schema(name = "role", description = "User's role")
        Roles role,

        @Schema(name = "salesId", description = "Identifier of the sales point this profile belongs to")
        @NotNull
        Long salesId
) {
}
