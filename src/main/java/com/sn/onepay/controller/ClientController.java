package com.sn.onepay.controller;

import com.sn.onepay.dto.ClientDTO;
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
    public Page<ClientDTO> getClientByFilters() {
        return null;
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
