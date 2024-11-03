package com.sn.onepay.controller;

import com.sn.onepay.dto.PaymentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/v1/onepay/payment")
@RequiredArgsConstructor
public class PaymentController {

    @Operation(summary = "Create a new payment", description = "This endpoint is for creating a new payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDTO createPayment(@Parameter(description = "Payment body for creation", required = true) @RequestBody @Valid PaymentDTO payment) {
        return payment;
    }

    @Operation(summary = "Update a payment", description = "This endpoint is for updating a payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = "/{paymentId}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDTO updatePayment(@Parameter(description = "Payment body to update", required = true) @RequestBody @Valid PaymentDTO payment,
                                    @Parameter(description = "Payment id to update", required = true) @PathVariable(name = "paymentId") Long paymentId) {
        return payment;
    }

    @Operation(summary = "Get payment by filter", description = "This endpoint is for getting payment by filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<PaymentDTO> getPaymentByFilters() {
        return null;
    }

    @Operation(summary = "Delete a payment by id", description = "This endpoint is for deleting payment by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = "/{paymentId}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePayment(@Parameter(description = "Payment id to delete", required = true) @PathVariable(name = "paymentId") Long paymentId) {
        log.info("Delete payment by id {}", paymentId);
    }
}
