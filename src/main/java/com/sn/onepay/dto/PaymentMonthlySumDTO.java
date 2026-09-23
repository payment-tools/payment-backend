package com.sn.onepay.dto;

import com.sn.onepay.enumeration.Modules;
import io.swagger.v3.oas.annotations.media.Schema;

public record PaymentMonthlySumDTO(

        @Schema(name = "month", description = "Month of the aggregation, format YYYY-MM")
        String month,

        @Schema(name = "module", description = "Module the amount was consumed on")
        Modules module,

        @Schema(name = "totalAmount", description = "Sum of active payments for this month and module")
        Double totalAmount
) {
}
