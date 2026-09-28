package com.sn.onepay.dto;

import com.sn.onepay.enumeration.ProspectScore;
import com.sn.onepay.enumeration.ProspectStage;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ProspectDTO(

        @Schema(name = "id", description = "Prospect identifier")
        Long id,

        @Schema(name = "ref", description = "Prospect reference")
        String ref,

        @Schema(name = "companyName", description = "Prospect company name")
        String companyName,

        @Schema(name = "contactName", description = "Prospect contact person name")
        String contactName,

        @Schema(name = "email", description = "Prospect contact email")
        String email,

        @Schema(name = "phoneNumber", description = "Prospect contact phone number")
        String phoneNumber,

        @Schema(name = "stage", description = "Pipeline stage")
        ProspectStage stage,

        @Schema(name = "note", description = "Free-text note")
        String note,

        @Schema(name = "potentialEmployees", description = "Estimated number of employees")
        Integer potentialEmployees,

        @Schema(name = "assignedTo", description = "Name of the commercial assigned to this prospect")
        String assignedTo,

        @Schema(name = "score", description = "Lead score")
        ProspectScore score,

        @Schema(name = "active", description = "Whether the prospect is active")
        Boolean active,

        @Schema(name = "creationDate", description = "Prospect date of creation")
        LocalDateTime creationDate,

        @Schema(name = "modificationDate", description = "Prospect date of modification")
        LocalDateTime modificationDate

) {
}
