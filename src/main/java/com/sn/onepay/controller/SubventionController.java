package com.sn.onepay.controller;

import com.sn.onepay.dto.SubventionDTO;
import com.sn.onepay.enumeration.StateStatus;
import com.sn.onepay.services.SubventionService;
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
@RequestMapping(value = "/v1/onepay/subvention")
@RequiredArgsConstructor
public class SubventionController {

    final SubventionService subventionService;

    @Operation(summary = "Create a new subvention", description = "This endpoint is for creating a new subvention")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public SubventionDTO createSubvention(@Parameter(description = "Subvention body for creation", required = true) @RequestBody @Valid SubventionDTO subvention) {
        return subventionService.createSubvention(subvention);
    }

    @Operation(summary = "Update a subvention", description = "This endpoint is for updating a subvention")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = "/{subventionId}")
    @ResponseStatus(HttpStatus.OK)
    public SubventionDTO updateSubvention(@Parameter(description = "Subvention body to update", required = true) @RequestBody @Valid SubventionDTO subvention,
                                          @Parameter(description = "Subvention id to update", required = true) @PathVariable(name = "subventionId") Long subventionId) {
        return subventionService.updateSubvention(subvention, subventionId);
    }

    @Operation(summary = "Get subventions by filter", description = "This endpoint is for getting subventions by filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<SubventionDTO> getSubventionsByFilters(
            @Parameter(description = "Filter by subvention ID") @RequestParam(required = false) Long id,
            @Parameter(description = "Filter by reference (partial match)") @RequestParam(required = false) String ref,
            @Parameter(description = "Filter by employee percentage") @RequestParam(required = false) Double employeePercent,
            @Parameter(description = "Filter by employer percentage") @RequestParam(required = false) Double employerPercent,
            @Parameter(description = "Filter by partnership ID") @RequestParam(required = false) Long partnershipId,
            @Parameter(description = "Filter by employee group ID") @RequestParam(required = false) Long employeeGroupId,
            @Parameter(description = "Filter by status (ACTIVE or INACTIVE)") @RequestParam(required = false) StateStatus status,
            @Parameter(description = "Filter records created after this date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime creationDate,
            @Parameter(description = "Filter records modified before this date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime modificationDate,
            Pageable pageable) {
        return subventionService.getSubventionsByFilters(id, ref, employeePercent, employerPercent, partnershipId, employeeGroupId, status, creationDate, modificationDate, pageable);
    }

    @Operation(summary = "Delete a subvention by id", description = "This endpoint is for deleting a subvention by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = "/{subventionId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteSubvention(@Parameter(description = "Subvention id to delete", required = true) @PathVariable(name = "subventionId") Long subventionId) {
        subventionService.deleteSubvention(subventionId);
    }
}