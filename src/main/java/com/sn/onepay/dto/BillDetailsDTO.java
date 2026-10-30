package com.sn.onepay.dto;

import com.sn.onepay.enumeration.BillStatus;
import com.sn.onepay.enumeration.Modules;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/*En-tête d'une facture pour la génération du PDF par le frontend*/
public record BillDetailsDTO(

        @Schema(name = "ref", description = "Bill reference")
        String ref,

        @Schema(name = "period", description = "Billed month (e.g. 2026-10)")
        String period,

        @Schema(name = "startDate", description = "First day of the billed month, 00:00 UTC")
        LocalDateTime startDate,

        @Schema(name = "endDate", description = "Last day of the billed month, 23:59:59 UTC")
        LocalDateTime endDate,

        @Schema(name = "billStatus", description = "Bill status")
        BillStatus billStatus,

        @Schema(name = "totalAmount", description = "Total of the active payments of the month (FCFA)")
        Double totalAmount,

        @Schema(name = "enterprise", description = "Billed enterprise")
        EnterpriseInfo enterprise,

        @Schema(name = "sales", description = "Merchant (sales point) of the partnership")
        SalesInfo sales
) {

    public record EnterpriseInfo(String name, String address) {
    }

    public record SalesInfo(String name, String address, Modules type) {
    }
}
