package com.sn.onepay.keycloak;

import com.sn.onepay.enumeration.Roles;

/*Champs d'une fiche utilisateur (Client, Cashier, EnterpriseProfile, SalesProfile) reportés dans Keycloak*/
public record AccountFields(String username, String email, String firstname, String lastname, Roles role, boolean active) {
}
