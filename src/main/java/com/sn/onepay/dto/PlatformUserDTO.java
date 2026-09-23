package com.sn.onepay.dto;

import com.sn.onepay.enumeration.PlatformUserType;
import com.sn.onepay.enumeration.Roles;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record PlatformUserDTO(

        @Schema(name = "userType", description = "Source entity of this user (CLIENT, CASHIER, ENTERPRISE_PROFILE, SALES_PROFILE)")
        PlatformUserType userType,

        @Schema(name = "id", description = "Identifier of the user within its source entity (unique together with userType, not globally)")
        Long id,

        @Schema(name = "ref", description = "User reference")
        String ref,

        @Schema(name = "firstname", description = "User firstname")
        String firstname,

        @Schema(name = "lastname", description = "User lastname")
        String lastname,

        @Schema(name = "username", description = "User username")
        String username,

        @Schema(name = "email", description = "User email")
        String email,

        @Schema(name = "phoneNumber", description = "User phone number")
        String phoneNumber,

        @Schema(name = "role", description = "User role")
        Roles role,

        @Schema(name = "organizationId", description = "Identifier of the enterprise or sales point this user belongs to")
        Long organizationId,

        @Schema(name = "organizationName", description = "Name of the enterprise or sales point this user belongs to")
        String organizationName,

        @Schema(name = "organizationType", description = "ENTERPRISE or SALES")
        String organizationType,

        @Schema(name = "active", description = "Whether the user is active")
        Boolean active,

        @Schema(name = "creationDate", description = "User date of creation")
        LocalDateTime creationDate,

        @Schema(name = "modificationDate", description = "User date of modification")
        LocalDateTime modificationDate

) {
}
