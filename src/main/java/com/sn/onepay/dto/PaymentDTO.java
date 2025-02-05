package com.sn.onepay.dto;

import com.sn.onepay.enumeration.Modules;
import com.sn.onepay.enumeration.StateStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PaymentDTO(

        @Schema(name = "id", description = "Payment identifier")
        Long id,

        @Schema(name = "ref", description = "Payment reference")
        String ref,

        @Schema(name = "client", description = "Client who made the payment")
        @NotNull(message = "Client can not be null")
        ClientDTO client,

        @Schema(name = "cashier", description = "Cashier who receive the payment")
        @NotNull(message = "Cashier can not be null")
        CashierDTO cashier,

        @Schema(name = "amount", description = "Amount of the payment")
        @NotNull(message = "Amount can not be null")
        Double amount,

        @Schema(name = "status", description = "Status of the payment")
        @NotNull(message = "Payment status can not be null")
        StateStatus status,

        @Schema(name = "module", description = "Module of the payment")
        @NotNull(message = "Payment module can not be null")
        Modules module,

        @Schema(name = "paymentDate", description = "Date of the payment made")
        LocalDateTime paymentDate,

        @Schema(name = "creationDate", description = "Payment date of creation")
        LocalDateTime creationDate,

        @Schema(name = "modificationDate", description = "Payment date of modification")
        LocalDateTime modificationDate
) {
}
