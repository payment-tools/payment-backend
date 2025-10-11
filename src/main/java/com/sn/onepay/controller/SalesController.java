package com.sn.onepay.controller;

import com.sn.onepay.dto.SalesDTO;
import com.sn.onepay.enumeration.Modules;
import com.sn.onepay.services.SalesService;
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
import org.springframework.web.bind.annotation.CrossOrigin;
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
@RequestMapping(value = "/v1/onepay/sales")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SalesController {

    final SalesService salesService;

    @Operation(summary = "Create a new sales", description = "This endpoint is for creating a new sales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public SalesDTO createSales(@Parameter(description = "Sales body for creation", required = true) @RequestBody @Valid SalesDTO sales) {
        return salesService.createSales(sales);
    }

    @Operation(summary = "Update a sales", description = "This endpoint is for updating a sales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = "/{salesId}")
    @ResponseStatus(HttpStatus.OK)
    public SalesDTO updateSales(@Parameter(description = "Sales body to update", required = true) @RequestBody @Valid SalesDTO sales,
                                @Parameter(description = "Sales id to update", required = true) @PathVariable(name = "salesId") Long salesId) {
        return salesService.updateSales(sales, salesId);
    }

    @Operation(summary = "Get sales by filters", description = "This endpoint is for getting sales by filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<SalesDTO> getSalesByFilters(@Parameter(description = "") @RequestParam(required = false) Long id,
                                            @Parameter(description = "") @RequestParam(required = false) String ref,
                                            @Parameter(description = "") @RequestParam(required = false) String name,
                                            @Parameter(description = "") @RequestParam(required = false) Modules type,
                                            @Parameter(description = "") @RequestParam(required = false) String address,
                                            @Parameter(description = "") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime creationDate,
                                            @Parameter(description = "") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime modificationDate,
                                            Pageable pageable) {
        return salesService.getSalesByFilters(id, ref, name, type, address, creationDate, modificationDate, pageable);
    }

    @Operation(summary = "Delete a sales by id", description = "This endpoint is for deleting sales by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = "/{salesId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteSales(@Parameter(description = "Sales id to delete", required = true) @PathVariable(name = "salesId") Long salesId) {
        salesService.deleteSales(salesId);
    }
}
