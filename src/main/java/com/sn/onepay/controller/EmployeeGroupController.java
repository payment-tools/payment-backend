package com.sn.onepay.controller;

import com.sn.onepay.dto.EmployeeGroupCreateDTO;
import com.sn.onepay.dto.EmployeeGroupDTO;
import com.sn.onepay.dto.EmployeeGroupUpdateDTO;
import com.sn.onepay.services.EmployeeGroupService;
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
@RequestMapping(value = "/v1/onepay/employee-group")
@RequiredArgsConstructor
public class EmployeeGroupController {

    final EmployeeGroupService employeeGroupService;

    @Operation(summary = "Create a new employee group", description = "This endpoint is for creating a new employee group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeGroupDTO createEmployeeGroup(@Parameter(description = "EmployeeGroup body for creation", required = true) @RequestBody @Valid EmployeeGroupCreateDTO employeeGroup) {
        return employeeGroupService.createEmployeeGroup(employeeGroup);
    }

    @Operation(summary = "Update an employee group", description = "This endpoint is for updating an employee group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = "/{employeeGroupId}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeGroupDTO updateEmployeeGroup(@Parameter(description = "EmployeeGroup body to update", required = true) @RequestBody @Valid EmployeeGroupUpdateDTO employeeGroup,
                                                @Parameter(description = "EmployeeGroup id to update", required = true) @PathVariable(name = "employeeGroupId") Long employeeGroupId) {
        return employeeGroupService.updateEmployeeGroup(employeeGroup, employeeGroupId);
    }

    @Operation(summary = "Get employee groups by filter", description = "This endpoint is for getting employee groups by filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<EmployeeGroupDTO> getEmployeeGroupsByFilters(
            @Parameter(description = "Filter by employee group ID") @RequestParam(required = false) Long id,
            @Parameter(description = "Filter by reference (partial match)") @RequestParam(required = false) String ref,
            @Parameter(description = "Filter by name (partial match)") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by enterprise ID") @RequestParam(required = false) Long enterpriseId,
            @Parameter(description = "Filter by active status (true or false)") @RequestParam(required = false) Boolean active,
            @Parameter(description = "Filter records created after this date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime creationDate,
            @Parameter(description = "Filter records modified before this date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime modificationDate,
            Pageable pageable) {
        return employeeGroupService.getEmployeeGroupsByFilters(id, ref, name, enterpriseId, active, creationDate, modificationDate, pageable);
    }

    @Operation(summary = "Delete an employee group by id", description = "This endpoint is for deleting an employee group by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = "/{employeeGroupId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteEmployeeGroup(@Parameter(description = "EmployeeGroup id to delete", required = true) @PathVariable(name = "employeeGroupId") Long employeeGroupId) {
        employeeGroupService.deleteEmployeeGroup(employeeGroupId);
    }
}