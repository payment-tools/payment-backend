package com.sn.onepay.controller;

import com.sn.onepay.dto.EnterpriseProfileDTO;
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
@RequestMapping(value = "/v1/onepay/enterpriseProfile")
@RequiredArgsConstructor
public class EnterpriseProfileController {

    @Operation(summary = "Create a new enterprise profile", description = "This endpoint is for creating a new enterprise profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public EnterpriseProfileDTO createEnterpriseProfile(@Parameter(description = "Enterprise profile body for creation", required = true) @RequestBody @Valid EnterpriseProfileDTO enterpriseProfile) {
        return enterpriseProfile;
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
        return enterpriseProfile;
    }

    @Operation(summary = "Get enterprise Profile by filter", description = "This endpoint is for getting enterprise Profile by filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<EnterpriseProfileDTO> getEnterpriseProfileByFilters() {
        return null;
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
        log.info("Delete enterpriseProfile by id {}", enterpriseProfileId);
    }

}
