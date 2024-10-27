package com.sn.onepay.dto;

import com.sn.onepay.entity.Partnership;
import com.sn.onepay.enumeration.BillStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record BillsDTO(

        @Schema(name = "id", description = "Bills identifier")
        Long id,

        @Schema(name = "ref", description = "Bills reference")
        String ref,

        @Schema(name = "partnership", description = "Bills partnership")
        Partnership partnership,

        @Schema(name = "startDate", description = "Bills start date")
        LocalDateTime startDate,

        @Schema(name = "endDate", description = "Bills end date")
        LocalDateTime endDate,

        @Schema(name = "totalAmount", description = "Bills total amount")
        Double totalAmount,

        @Schema(name = "billStatus", description = "Bills status")
        BillStatus billStatus,

        @Schema(name = "creationDate", description = "Bills date of creation")
        LocalDateTime creationDate,

        @Schema(name = "modificationDate", description = "Bills date of modification")
        LocalDateTime modificationDate

) {
}
