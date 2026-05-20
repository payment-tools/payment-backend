package com.sn.onepay.controller;

import com.sn.onepay.dto.SalesProfileDTO;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.enumeration.StateStatus;
import com.sn.onepay.services.SalesProfileService;
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
@RequestMapping(value = "/v1/onepay/salesProfile")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SalesProfileController {

    final SalesProfileService salesProfileService;

    @Operation(summary = "Create a new sales profile", description = "This endpoint is for creating a new sales profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public SalesProfileDTO createSalesProfile(@Parameter(description = "Sales profile body for creation", required = true) @RequestBody @Valid SalesProfileDTO salesProfile) {
        return salesProfileService.createSalesProfile(salesProfile);
    }

    @Operation(summary = "Update a sales Profile", description = "This endpoint is for updating a sales Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = "/{salesProfileId}")
    @ResponseStatus(HttpStatus.OK)
    public SalesProfileDTO updateSalesProfile(@Parameter(description = "Sales Profile body to update", required = true) @RequestBody @Valid SalesProfileDTO salesProfile,
                                              @Parameter(description = "Sales Profile id to update", required = true) @PathVariable(name = "salesProfileId") Long salesProfileId) {
        return salesProfileService.updateSalesProfile(salesProfile, salesProfileId);
    }

    @Operation(summary = "Get sales Profile by filters", description = "This endpoint is for getting sales Profile by filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<SalesProfileDTO> getSalesProfileByFilters(@Parameter(description = "Filter by sales profile ID") @RequestParam(required = false) Long id,
                                                          @Parameter(description = "Filter by reference (partial match)") @RequestParam(required = false) String ref,
                                                          @Parameter(description = "Filter by first name (partial match)") @RequestParam(required = false) String firstname,
                                                          @Parameter(description = "Filter by last name (partial match)") @RequestParam(required = false) String lastname,
                                                          @Parameter(description = "Filter by username (partial match)") @RequestParam(required = false) String username,
                                                          @Parameter(description = "Filter by email (partial match)") @RequestParam(required = false) String email,
                                                          @Parameter(description = "Filter by phone number (partial match)") @RequestParam(required = false) String phoneNumber,
                                                          @Parameter(description = "Filter by role") @RequestParam(required = false) Roles role,
                                                          @Parameter(description = "Filter by sales ID") @RequestParam(required = false) Long salesId,
                                                          @Parameter(description = "Filter by status (ACTIVE or INACTIVE)") @RequestParam(required = false) StateStatus status,
                                                          @Parameter(description = "Filter records created after this date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime creationDate,
                                                          @Parameter(description = "Filter records modified before this date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime modificationDate,
                                                          Pageable pageable) {
        return salesProfileService.getSalesProfilesByFilters(id, ref, firstname, lastname, username, email, phoneNumber, role, salesId, status, creationDate, modificationDate, pageable);
    }

    @Operation(summary = "Delete a sales Profile by id", description = "This endpoint is for deleting sales Profile by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = "/{salesProfileId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteSalesProfile(@Parameter(description = "Sales Profile id to delete", required = true) @PathVariable(name = "salesProfileId") Long salesProfileId) {
        salesProfileService.deleteSalesProfile(salesProfileId);
    }
}
