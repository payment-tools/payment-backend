package com.sn.onepay.mail;

/*Contenu de l'e-mail envoyé à la création d'un compte de connexion*/
public record AccountInvitation(String email, String firstname, String lastname, String username, String temporaryPassword) {
}
