package com.sn.onepay.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/*Rapport d'import Excel des employés : lignes créées et lignes en erreur (numéro de ligne Excel)*/
public record ClientImportReportDTO(

        @Schema(name = "totalRows", description = "Number of non-empty data rows read from the file")
        int totalRows,

        @Schema(name = "created", description = "Rows imported successfully")
        List<CreatedRow> created,

        @Schema(name = "errors", description = "Rows rejected, with the reason")
        List<RowError> errors
) {

    public record CreatedRow(int row, Long id, String username) {
    }

    public record RowError(int row, String message) {
    }
}
