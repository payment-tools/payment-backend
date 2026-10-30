package com.sn.onepay.dto;

import com.sn.onepay.enumeration.DevicePlatform;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/*Jeton FCM envoyé par l'app mobile après la connexion (et à chaque renouvellement du jeton)*/
public record DeviceTokenDTO(

        @Schema(name = "token", description = "Firebase Cloud Messaging registration token of the device")
        @NotBlank
        @Size(max = 512)
        String token,

        @Schema(name = "platform", description = "ANDROID or IOS")
        @NotNull
        DevicePlatform platform
) {
}
