package com.sn.onepay.controller;

import com.sn.onepay.dto.PlatformUserDTO;
import com.sn.onepay.enumeration.PlatformUserType;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.services.PlatformUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping(value = "/v1/onepay/platformUser")
@RequiredArgsConstructor
public class PlatformUserController {

    final PlatformUserService platformUserService;

    @Operation(summary = "Get platform users by filters", description = "This endpoint returns a cross-entity view of Client/Cashier/EnterpriseProfile/SalesProfile accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<PlatformUserDTO> getPlatformUsersByFilters(@Parameter(description = "Search across firstname/lastname/username/email") @RequestParam(required = false) String search,
                                                             @Parameter(description = "Filter by role") @RequestParam(required = false) Roles role,
                                                             @Parameter(description = "Filter by source entity type") @RequestParam(required = false) PlatformUserType userType,
                                                             @Parameter(description = "Filter by active status (true or false)") @RequestParam(required = false) Boolean active,
                                                             Pageable pageable) {
        return platformUserService.getPlatformUsersByFilters(search, role, userType, active, pageable);
    }

    @Operation(summary = "Get a platform user by type and id", description = "This endpoint returns a single user given its source entity type and id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{userType}/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PlatformUserDTO getPlatformUserByTypeAndId(@Parameter(description = "Source entity type", required = true) @PathVariable(name = "userType") PlatformUserType userType,
                                                        @Parameter(description = "User id within its source entity", required = true) @PathVariable(name = "id") Long id) {
        return platformUserService.getPlatformUserByTypeAndId(userType, id);
    }
}
