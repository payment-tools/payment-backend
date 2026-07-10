package com.sn.onepay.dto;

import com.sn.onepay.enumeration.BillStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BillsDTO(

        @Schema(name = "id", description = "Bills identifier")
        Long id,

        @Schema(name = "ref", description = "Bills reference")
        String ref,

        @Schema(name = "partnership", description = "Bills partnership")
        @NotNull(message = "Partnership can not be null")
        PartnershipDTO partnership,

        @Schema(name = "startDate", description = "Bills start date")
        @NotNull(message = "Start date can not be null")
        LocalDateTime startDate,

        @Schema(name = "endDate", description = "Bills end date")
        @NotNull(message = "End date can not be null")
        LocalDateTime endDate,

        @Schema(name = "totalAmount", description = "Bills total amount")
        @NotNull(message = "Total amount can not be null")
        Double totalAmount,

        @Schema(name = "billStatus", description = "Bills status")
        @NotNull(message = "Bill status can not be null")
        BillStatus billStatus,

        @Schema(name = "period", description = "Billed period label (e.g. 2026-07)")
        @NotNull(message = "Period can not be null")
        String period,

        @Schema(name = "active", description = "Whether the bill is active")
        Boolean active,

        @Schema(name = "creationDate", description = "Bills date of creation")
        LocalDateTime creationDate,

        @Schema(name = "modificationDate", description = "Bills date of modification")
        LocalDateTime modificationDate

) {
}
