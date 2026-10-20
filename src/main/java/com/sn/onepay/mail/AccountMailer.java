package com.sn.onepay.mail;

/*Envoi des identifiants d'un compte nouvellement créé. Fournisseur choisi par onepay.mail.provider*/
public interface AccountMailer {

    void sendInvitation(AccountInvitation invitation);
}
