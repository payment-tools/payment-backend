package com.sn.onepay.controller;

import com.sn.onepay.dto.ClientDTO;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.services.ClientService;
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
@RequestMapping(value = "/v1/onepay/client")
@RequiredArgsConstructor
public class ClientController {

    final ClientService clientService;

    @Operation(summary = "Create a new client", description = "This endpoint is for creating a new client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDTO createClient(@Parameter(description = "Client body for creation", required = true) @RequestBody @Valid ClientDTO client) {
        return clientService.createClient(client);
    }

    @Operation(summary = "Update a client", description = "This endpoint is for updating a client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = "/{clientId}")
    @ResponseStatus(HttpStatus.OK)
    public ClientDTO updateClient(@Parameter(description = "Client body to update", required = true) @RequestBody @Valid ClientDTO client,
                                  @Parameter(description = "Client id to update", required = true) @PathVariable(name = "clientId") Long clientId) {
        return clientService.updateClient(client, clientId);
    }

    @Operation(summary = "Get client by filter", description = "This endpoint is for getting client by filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ClientDTO> getClientByFilters(
            @Parameter(description = "Filter by client ID") @RequestParam(required = false) Long id,
            @Parameter(description = "Filter by reference (partial match)") @RequestParam(required = false) String ref,
            @Parameter(description = "Filter by first name (partial match)") @RequestParam(required = false) String firstname,
            @Parameter(description = "Filter by last name (partial match)") @RequestParam(required = false) String lastname,
            @Parameter(description = "Filter by username (partial match)") @RequestParam(required = false) String username,
            @Parameter(description = "Filter by email (partial match)") @RequestParam(required = false) String email,
            @Parameter(description = "Filter by phone number (partial match)") @RequestParam(required = false) String phoneNumber,
            @Parameter(description = "Filter by role") @RequestParam(required = false) Roles role,
            @Parameter(description = "Filter by enterprise ID") @RequestParam(required = false) Long enterpriseId,
            @Parameter(description = "Filter by active status (true or false)") @RequestParam(required = false) Boolean active,
            @Parameter(description = "Filter records created after this date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime creationDate,
            @Parameter(description = "Filter records modified before this date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime modificationDate,
            Pageable pageable
    ) {
        return clientService.getClientsByFilters(id, ref, firstname, lastname, username, email, phoneNumber, role, enterpriseId, active, creationDate, modificationDate, pageable);
    }

    @Operation(summary = "Delete a client by id", description = "This endpoint is for deleting client by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = "/{clientId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteClient(@Parameter(description = "Client id to delete", required = true) @PathVariable(name = "clientId") Long clientId) {
        clientService.deleteClient(clientId);
    }
}
