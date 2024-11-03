package com.sn.onepay.controller;

import com.sn.onepay.dto.EnterpriseDTO;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.mapper.EnterpriseMapper;
import com.sn.onepay.repository.EnterpriseRepository;
import com.sn.onepay.services.EnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping(value = "/v1/onepay/enterprise")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EnterpriseController {

    final EnterpriseService enterpriseService;
    private final EnterpriseRepository enterpriseRepository;
    private final EnterpriseMapper enterpriseMapper;

    @Operation(summary = "Create a new enterprise", description = "This endpoint is for creating a new enterprise")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public EnterpriseDTO createEnterprise(@Parameter(description = "Enterprise body for creation", required = true) @RequestBody @Valid EnterpriseDTO enterprise) {
        return enterpriseService.createEnterprise(enterprise);
    }

    @Operation(summary = "Update a enterprise", description = "This endpoint is for updating a enterprise")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = "/{enterpriseId}")
    @ResponseStatus(HttpStatus.OK)
    public EnterpriseDTO updateEnterprise(@Parameter(description = "Enterprise body to update", required = true) @RequestBody @Valid EnterpriseDTO enterprise,
                                          @Parameter(description = "Enterprise id to update", required = true) @PathVariable(name = "enterpriseId") Long enterpriseId) {
        return enterpriseService.updateEnterprise(enterprise, enterpriseId);
    }

    @Operation(summary = "Get enterprise by filter", description = "This endpoint is for getting enterprise by filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Enterprise> getEnterpriseByFilters() {
        //TODO
        return  enterpriseRepository.findAll();
    }

    @Operation(summary = "Delete a enterprise by id", description = "This endpoint is for deleting enterprise by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = "/{enterpriseId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteEnterprise(@Parameter(description = "Enterprise id to delete", required = true) @PathVariable(name = "enterpriseId") Long enterpriseId) {
        enterpriseService.deleteEnterprise(enterpriseId);
    }
}
