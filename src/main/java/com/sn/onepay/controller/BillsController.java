package com.sn.onepay.controller;

import com.sn.onepay.dto.BillsDTO;
import com.sn.onepay.enumeration.BillStatus;
import com.sn.onepay.services.BillsService;
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
@RequestMapping(value = "/v1/onepay/bills")
@RequiredArgsConstructor
public class BillsController {

    final BillsService billsService;

    @Operation(summary = "Create a new bill", description = "This endpoint is for creating a new bill")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public BillsDTO createBills(@Parameter(description = "Bills body for creation", required = true) @RequestBody @Valid BillsDTO bills) {
        return billsService.createBills(bills);
    }

    @Operation(summary = "Update a bill", description = "This endpoint is for updating a bill")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = "/{billsId}")
    @ResponseStatus(HttpStatus.OK)
    public BillsDTO updateBills(@Parameter(description = "Bills body to update", required = true) @RequestBody @Valid BillsDTO bills,
                                @Parameter(description = "Bills id to update", required = true) @PathVariable(name = "billsId") Long billsId) {
        return billsService.updateBills(bills, billsId);
    }

    @Operation(summary = "Get bills by filter", description = "This endpoint is for getting bills by filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<BillsDTO> getBillsByFilters(
            @Parameter(description = "Filter by bill ID") @RequestParam(required = false) Long id,
            @Parameter(description = "Filter by reference (partial match)") @RequestParam(required = false) String ref,
            @Parameter(description = "Filter by partnership ID") @RequestParam(required = false) Long partnershipId,
            @Parameter(description = "Filter by bill status (PAYED or UNPAYED)") @RequestParam(required = false) BillStatus billStatus,
            @Parameter(description = "Filter by billed period label (e.g. 2026-07)") @RequestParam(required = false) String period,
            @Parameter(description = "Filter by active status (true or false)") @RequestParam(required = false) Boolean active,
            @Parameter(description = "Filter bills starting after this date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Filter bills ending before this date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Filter records created after this date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime creationDate,
            @Parameter(description = "Filter records modified before this date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime modificationDate,
            Pageable pageable) {
        return billsService.getBillsByFilters(id, ref, partnershipId, billStatus, period, active, startDate, endDate, creationDate, modificationDate, pageable);
    }

    @Operation(summary = "Delete a bill by id", description = "This endpoint is for deleting a bill by id (logical deletion)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = "/{billsId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBills(@Parameter(description = "Bills id to delete", required = true) @PathVariable(name = "billsId") Long billsId) {
        billsService.deleteBills(billsId);
    }
}
