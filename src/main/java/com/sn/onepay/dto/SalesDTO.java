package com.sn.onepay.dto;

import com.sn.onepay.enumeration.Modules;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record SalesDTO(

        @Schema(name = "id", description = "Sales identifier")
        Long id,

        @Schema(name = "ref", description = "Sales reference")
        String ref,

        @Schema(name = "name", description = "Sales name")
        String name,

        @Schema(name = "type", description = "Type of sales")
        Modules type,

        @Schema(name = "creationDate", description = "Sales date of creation")
        LocalDateTime creationDate,

        @Schema(name = "modificationDate", description = "Sales date of modification")
        LocalDateTime modificationDate
) {
}
