package com.sn.onepay.controller;

import com.sn.onepay.dto.EnterpriseProfileDTO;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.enumeration.StateStatus;
import com.sn.onepay.services.EnterpriseProfileService;
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
@RequestMapping(value = "/v1/onepay/enterpriseProfile")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EnterpriseProfileController {

    final EnterpriseProfileService enterpriseProfileService;

    @Operation(summary = "Create a new enterprise profile", description = "This endpoint is for creating a new enterprise profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public EnterpriseProfileDTO createEnterpriseProfile(@Parameter(description = "Enterprise profile body for creation", required = true) @RequestBody @Valid EnterpriseProfileDTO enterpriseProfile) {
        return enterpriseProfileService.createEnterpriseProfile(enterpriseProfile);
    }

    @Operation(summary = "Update a enterprise Profile", description = "This endpoint is for updating a enterprise Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = "/{enterpriseProfileId}")
    @ResponseStatus(HttpStatus.OK)
    public EnterpriseProfileDTO updateEnterpriseProfile(@Parameter(description = "Enterprise Profile body to update", required = true) @RequestBody @Valid EnterpriseProfileDTO enterpriseProfile,
                                                        @Parameter(description = "Enterprise Profile id to update", required = true) @PathVariable(name = "enterpriseProfileId") Long enterpriseProfileId) {
        return enterpriseProfileService.updateEnterpriseProfile(enterpriseProfile, enterpriseProfileId);
    }

    @Operation(summary = "Get enterprise Profile by filter", description = "This endpoint is for getting enterprise Profile by filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<EnterpriseProfileDTO> getEnterpriseProfileByFilters(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String ref,
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String lastname,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) Roles role,
            @RequestParam(required = false) Long enterpriseId,
            @RequestParam(required = false) StateStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime creationDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime modificationDate,
            Pageable pageable
    ) {
        return enterpriseProfileService.getEnterpriseProfilesByFilters(id, ref, firstname, lastname, username, email, phoneNumber, role, enterpriseId, status, creationDate, modificationDate, pageable);
    }

    @Operation(summary = "Delete a enterprise Profile by id", description = "This endpoint is for deleting enterprise Profile by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = "/{enterpriseProfileId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteEnterpriseProfile(@Parameter(description = "Enterprise Profile id to delete", required = true) @PathVariable(name = "enterpriseProfileId") Long enterpriseProfileId) {
        enterpriseProfileService.deleteEnterpriseProfile(enterpriseProfileId);
    }

}
