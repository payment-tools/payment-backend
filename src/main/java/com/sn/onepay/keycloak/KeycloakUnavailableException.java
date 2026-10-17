package com.sn.onepay.keycloak;

/*Keycloak injoignable ou réponse inattendue de son API d'administration*/
public class KeycloakUnavailableException extends RuntimeException {

    public KeycloakUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
