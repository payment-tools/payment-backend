package com.sn.onepay.dto;

import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.utils.PhoneNumbers;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ClientCreateDTO(

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
        @NotBlank
        @Pattern(regexp = PhoneNumbers.SENEGAL_PATTERN, message = PhoneNumbers.FORMAT_MESSAGE)
        String phoneNumber,

        @Schema(name = "role", description = "Client Role")
        Roles role,

        @Schema(name = "enterpriseId", description = "Identifier of the enterprise this client belongs to")
        @NotNull
        Long enterpriseId
) {
}
