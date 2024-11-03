package com.sn.onepay.controller;

import com.sn.onepay.dto.PartnershipDTO;
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
@RequestMapping(value = "/v1/onepay/partnership")
@RequiredArgsConstructor
public class PartnershipController {

    @Operation(summary = "Create a new partnership", description = "This endpoint is for creating a new partnership")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public PartnershipDTO createPartnership(@Parameter(description = "Partnership body for creation", required = true) @RequestBody @Valid PartnershipDTO partnership) {
        return partnership;
    }

    @Operation(summary = "Update a partnership", description = "This endpoint is for updating a partnership")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = "/{partnershipId}")
    @ResponseStatus(HttpStatus.OK)
    public PartnershipDTO updatePartnership(@Parameter(description = "Partnership body to update", required = true) @RequestBody @Valid PartnershipDTO partnership,
                                            @Parameter(description = "Partnership id to update", required = true) @PathVariable(name = "partnershipId") Long partnershipId) {
        return partnership;
    }

    @Operation(summary = "Get partnership by filter", description = "This endpoint is for getting partnership by filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<PartnershipDTO> getPartnershipByFilters() {
        return null;
    }

    @Operation(summary = "Delete a partnership by id", description = "This endpoint is for deleting partnership by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = "/{partnershipId}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePartnership(@Parameter(description = "Partnership id to delete", required = true) @PathVariable(name = "partnershipId") Long partnershipId) {
        log.info("Delete partnership by id {}", partnershipId);
    }
}
