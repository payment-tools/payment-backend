package com.sn.onepay.dto;

import com.sn.onepay.enumeration.ProspectScore;
import com.sn.onepay.enumeration.ProspectStage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProspectCreateDTO(

        @Schema(name = "companyName", description = "Prospect company name")
        @NotNull(message = "Company name can not be null")
        String companyName,

        @Schema(name = "contactName", description = "Prospect contact person name")
        String contactName,

        @Schema(name = "email", description = "Prospect contact email")
        String email,

        @Schema(name = "phoneNumber", description = "Prospect contact phone number")
        String phoneNumber,

        @Schema(name = "stage", description = "Pipeline stage (defaults to DECOUVERTE)")
        ProspectStage stage,

        @Schema(name = "note", description = "Free-text note")
        String note,

        @Schema(name = "potentialEmployees", description = "Estimated number of employees")
        Integer potentialEmployees,

        @Schema(name = "assignedTo", description = "Name of the commercial assigned to this prospect")
        String assignedTo,

        @Schema(name = "score", description = "Lead score (defaults to COLD)")
        ProspectScore score
) {
}
