package com.sn.onepay.controller;

import com.sn.onepay.dto.CashierDTO;
import com.sn.onepay.services.CashierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping(value = "/v1/onepay/cashier")
@RequiredArgsConstructor
public class CashierController {

    final CashierService cashierService;

    @Operation(summary = "Create a new cashier", description = "This endpoint is for creating a new cashier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public CashierDTO createCashier(@Parameter(description = "Cashier body for creation", required = true) @RequestBody @Valid CashierDTO cashier) {
        return cashierService.createCashier(cashier);
    }

    @Operation(summary = "Update a cashier", description = "This endpoint is for updating a cashier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = "/{cashierId}")
    @ResponseStatus(HttpStatus.OK)
    public CashierDTO updateCashier(@Parameter(description = "Cashier body to update", required = true) @RequestBody @Valid CashierDTO cashier,
                                    @Parameter(description = "Cashier id to update", required = true) @PathVariable(name = "cashierId") Long cashierId) {
        return cashierService.updateCashier(cashier, cashierId);
    }

    @Operation(summary = "Get cashier by filter", description = "This endpoint is for getting cashier by filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<CashierDTO> getCashierByFilters() {
        return null;
    }

    @Operation(summary = "Delete a cashier by id", description = "This endpoint is for deleting cashier by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = "/{cashierId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCashier(@Parameter(description = "Cashier id to delete", required = true) @PathVariable(name = "cashierId") Long cashierId) {
        cashierService.deleteCashier(cashierId);
    }

}
