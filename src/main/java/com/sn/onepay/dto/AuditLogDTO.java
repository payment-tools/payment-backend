package com.sn.onepay.dto;

import com.sn.onepay.enumeration.AuditSeverity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record AuditLogDTO(

        @Schema(name = "id", description = "Audit log entry identifier")
        Long id,

        @Schema(name = "eventType", description = "Human-readable event label")
        String eventType,

        @Schema(name = "httpMethod", description = "HTTP method of the audited request")
        String httpMethod,

        @Schema(name = "path", description = "Path of the audited request")
        String path,

        @Schema(name = "actorUsername", description = "Username of the authenticated actor")
        String actorUsername,

        @Schema(name = "actorRole", description = "Role of the authenticated actor")
        String actorRole,

        @Schema(name = "ipAddress", description = "IP address of the caller")
        String ipAddress,

        @Schema(name = "severity", description = "Event severity")
        AuditSeverity severity,

        @Schema(name = "details", description = "Free-text details")
        String details,

        @Schema(name = "active", description = "Whether the entry is active")
        Boolean active,

        @Schema(name = "creationDate", description = "Event timestamp")
        LocalDateTime creationDate,

        @Schema(name = "modificationDate", description = "Entry date of modification")
        LocalDateTime modificationDate

) {
}
