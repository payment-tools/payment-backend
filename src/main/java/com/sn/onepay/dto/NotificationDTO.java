package com.sn.onepay.dto;

import com.sn.onepay.enumeration.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record NotificationDTO(

        @Schema(name = "id", description = "Notification id")
        Long id,

        @Schema(name = "type", description = "PAYMENT_CREATED or PAYMENT_CANCELLED")
        NotificationType type,

        @Schema(name = "title", description = "Short title, also used as push title")
        String title,

        @Schema(name = "message", description = "Notification text, also used as push body")
        String message,

        @Schema(name = "paymentId", description = "Related payment")
        Long paymentId,

        @Schema(name = "read", description = "Whether the user has read the notification")
        Boolean read,

        @Schema(name = "creationDate", description = "Creation date (UTC)")
        LocalDateTime creationDate
) {
}
