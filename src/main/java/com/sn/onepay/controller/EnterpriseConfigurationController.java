package com.sn.onepay.controller;

import com.sn.onepay.dto.EnterpriseConfigurationDTO;
import com.sn.onepay.services.EnterpriseConfigurationService;
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
import org.springframework.web.bind.annotation.CrossOrigin;
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
@RequestMapping(value = "/v1/onepay/enterpriseConfiguration")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EnterpriseConfigurationController {

    final EnterpriseConfigurationService enterpriseConfigurationService;

    @Operation(summary = "Create a new enterprise configuration", description = "This endpoint is for creating a new enterprise configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public EnterpriseConfigurationDTO createEnterpriseConfiguration(@Parameter(description = "Enterprise configuration body for creation", required = true) @RequestBody @Valid EnterpriseConfigurationDTO enterpriseConfiguration) {
        return enterpriseConfigurationService.createEnterpriseConfiguration(enterpriseConfiguration);
    }

    @Operation(summary = "Update a enterprise configuration", description = "This endpoint is for updating a enterprise configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = "/{enterpriseConfigurationId}")
    @ResponseStatus(HttpStatus.OK)
    public EnterpriseConfigurationDTO updateEnterpriseConfiguration(@Parameter(description = "Enterprise Configuration body to update", required = true) @RequestBody @Valid EnterpriseConfigurationDTO enterpriseConfiguration,
                                                                    @Parameter(description = "Enterprise Configuration id to update", required = true) @PathVariable(name = "enterpriseConfigurationId") Long enterpriseConfigurationId) {
        return enterpriseConfigurationService.updateEnterpriseConfiguration(enterpriseConfiguration, enterpriseConfigurationId);
    }

    @Operation(summary = "Get enterprise Configuration by filter", description = "This endpoint is for getting enterprise Configuration by filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<EnterpriseConfigurationDTO> getEnterpriseConfigurationByFilters() {
        return null;
    }

    @Operation(summary = "Delete a enterprise Configuration by id", description = "This endpoint is for deleting enterprise Configuration by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = "/{enterpriseConfigurationId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteEnterpriseConfiguration(@Parameter(description = "Enterprise Configuration id to delete", required = true) @PathVariable(name = "enterpriseConfigurationId") Long enterpriseConfigurationId) {
        enterpriseConfigurationService.deleteEnterpriseConfiguration(enterpriseConfigurationId);
    }
}
