package com.sn.onepay.controller;

import com.sn.onepay.dto.SalesConfigurationsDTO;
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
@RequestMapping(value = "/v1/onepay/salesConfiguration")
@RequiredArgsConstructor
public class SalesConfigurationsController {

    @Operation(summary = "Create a new sales configuration", description = "This endpoint is for creating a new sales configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public SalesConfigurationsDTO createSalesConfiguration(@Parameter(description = "Sales configuration body for creation", required = true) @RequestBody @Valid SalesConfigurationsDTO salesConfigurations) {
        return salesConfigurations;
    }

    @Operation(summary = "Update a sales Configurations", description = "This endpoint is for updating a sales Configurations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = "/{salesConfigurationsId}")
    @ResponseStatus(HttpStatus.OK)
    public SalesConfigurationsDTO updateSalesConfigurations(@Parameter(description = "Sales Configurations body to update", required = true) @RequestBody @Valid SalesConfigurationsDTO salesConfigurations,
                                                            @Parameter(description = "Sales Configurations id to update", required = true) @PathVariable(name = "salesConfigurationsId") Long salesConfigurationsId) {
        return salesConfigurations;
    }

    @Operation(summary = "Get sales Configurations by filter", description = "This endpoint is for getting sales Configurations by filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<SalesConfigurationsDTO> getSalesConfigurationsByFilters() {
        return null;
    }

    @Operation(summary = "Delete a sales Configurations by id", description = "This endpoint is for deleting sales Configurations by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = "/{salesConfigurationsId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteSalesConfigurations(@Parameter(description = "Sales Configurations id to delete", required = true) @PathVariable(name = "salesConfigurationsId") Long salesConfigurationsId) {
        log.info("Delete salesConfigurations by id {}", salesConfigurationsId);
    }
}
