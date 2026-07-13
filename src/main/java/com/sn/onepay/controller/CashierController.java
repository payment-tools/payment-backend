package com.sn.onepay.controller;

import com.sn.onepay.dto.CashierDTO;
import com.sn.onepay.enumeration.Roles;
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
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

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
    public Page<CashierDTO> getCashierByFilters(
            @Parameter(description = "Filter by cashier ID") @RequestParam(required = false) Long id,
            @Parameter(description = "Filter by reference (partial match)") @RequestParam(required = false) String ref,
            @Parameter(description = "Filter by first name (partial match)") @RequestParam(required = false) String firstname,
            @Parameter(description = "Filter by last name (partial match)") @RequestParam(required = false) String lastname,
            @Parameter(description = "Filter by username (partial match)") @RequestParam(required = false) String username,
            @Parameter(description = "Filter by email (partial match)") @RequestParam(required = false) String email,
            @Parameter(description = "Filter by phone number (partial match)") @RequestParam(required = false) String phoneNumber,
            @Parameter(description = "Filter by role") @RequestParam(required = false) Roles role,
            @Parameter(description = "Filter by sales ID") @RequestParam(required = false) Long salesId,
            @Parameter(description = "Filter by active status (true or false)") @RequestParam(required = false) Boolean active,
            @Parameter(description = "Filter records created after this date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime creationDate,
            @Parameter(description = "Filter records modified before this date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime modificationDate,
            Pageable pageable
    ) {
        return cashierService.getCashiersByFilter(id, ref, firstname, lastname, username, email, phoneNumber, role, salesId, active, creationDate, modificationDate, pageable);
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
