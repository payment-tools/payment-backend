package com.sn.onepay.controller;

import com.sn.onepay.dto.FirstPasswordChangeDTO;
import com.sn.onepay.services.PasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(value = "/v1/onepay/auth")
@RequiredArgsConstructor
public class PasswordController {

    PasswordService passwordService;

    @Operation(summary = "Première connexion : remplace le mot de passe temporaire",
            description = "Public (l'utilisateur n'a pas encore de jeton) : le mot de passe temporaire fait office de preuve d'identité")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mot de passe définitif enregistré"),
            @ApiResponse(responseCode = "400", description = "Requête incomplète ou nouveau mot de passe trop court"),
            @ApiResponse(responseCode = "422", description = "Identifiants incorrects, mot de passe identique ou refusé par la politique de sécurité"),
            @ApiResponse(responseCode = "502", description = "Keycloak injoignable")
    })
    @PostMapping(value = "/first-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeTemporaryPassword(@Valid @RequestBody FirstPasswordChangeDTO firstPasswordChangeDTO) {
        passwordService.changeTemporaryPassword(firstPasswordChangeDTO);
    }
}
