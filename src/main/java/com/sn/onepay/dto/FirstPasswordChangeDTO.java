package com.sn.onepay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FirstPasswordChangeDTO(

        @Schema(name = "username", description = "Keycloak username")
        @NotBlank(message = "Username can not be blank")
        String username,

        @Schema(name = "temporaryPassword", description = "Temporary password set by the administrator")
        @NotBlank(message = "Temporary password can not be blank")
        String temporaryPassword,

        @Schema(name = "newPassword", description = "New personal password (8 characters minimum)")
        @NotBlank(message = "New password can not be blank")
        @Size(min = 8, message = "New password must contain at least 8 characters")
        String newPassword
) {
}
