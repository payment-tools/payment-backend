package com.sn.onepay.controller;

import com.sn.onepay.dto.ProspectCreateDTO;
import com.sn.onepay.dto.ProspectDTO;
import com.sn.onepay.dto.ProspectUpdateDTO;
import com.sn.onepay.enumeration.ProspectScore;
import com.sn.onepay.enumeration.ProspectStage;
import com.sn.onepay.services.ProspectService;
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
@RequestMapping(value = "/v1/onepay/prospect")
@RequiredArgsConstructor
public class ProspectController {

    final ProspectService prospectService;

    @Operation(summary = "Create a new prospect", description = "This endpoint is for creating a new prospect")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ProspectDTO createProspect(@Parameter(description = "Prospect body for creation", required = true) @RequestBody @Valid ProspectCreateDTO prospect) {
        return prospectService.createProspect(prospect);
    }

    @Operation(summary = "Update a prospect", description = "This endpoint is for updating a prospect")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = "/{prospectId}")
    @ResponseStatus(HttpStatus.OK)
    public ProspectDTO updateProspect(@Parameter(description = "Prospect body to update", required = true) @RequestBody @Valid ProspectUpdateDTO prospect,
                                       @Parameter(description = "Prospect id to update", required = true) @PathVariable(name = "prospectId") Long prospectId) {
        return prospectService.updateProspect(prospect, prospectId);
    }

    @Operation(summary = "Get prospects by filters", description = "This endpoint is for getting prospects by filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ProspectDTO> getProspectsByFilters(@Parameter(description = "Filter by prospect ID") @RequestParam(required = false) Long id,
                                                    @Parameter(description = "Filter by reference (partial match)") @RequestParam(required = false) String ref,
                                                    @Parameter(description = "Filter by company name (partial match)") @RequestParam(required = false) String companyName,
                                                    @Parameter(description = "Filter by pipeline stage") @RequestParam(required = false) ProspectStage stage,
                                                    @Parameter(description = "Filter by lead score") @RequestParam(required = false) ProspectScore score,
                                                    @Parameter(description = "Filter by assigned commercial (partial match)") @RequestParam(required = false) String assignedTo,
                                                    @Parameter(description = "Filter by active status (true or false)") @RequestParam(required = false) Boolean active,
                                                    @Parameter(description = "Filter records created after this date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime creationDate,
                                                    @Parameter(description = "Filter records modified before this date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime modificationDate,
                                                    Pageable pageable) {
        return prospectService.getProspectsByFilters(id, ref, companyName, stage, score, assignedTo, active, creationDate, modificationDate, pageable);
    }

    @Operation(summary = "Get a prospect by id", description = "This endpoint returns a single prospect by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{prospectId}")
    @ResponseStatus(HttpStatus.OK)
    public ProspectDTO getProspectById(@Parameter(description = "Prospect id", required = true) @PathVariable(name = "prospectId") Long prospectId) {
        return prospectService.getProspectById(prospectId);
    }

    @Operation(summary = "Delete a prospect by id", description = "This endpoint is for deleting a prospect by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = "/{prospectId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteProspect(@Parameter(description = "Prospect id to delete", required = true) @PathVariable(name = "prospectId") Long prospectId) {
        prospectService.deleteProspect(prospectId);
    }
}
