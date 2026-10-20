package com.sn.onepay.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/*Aucun fournisseur configuré (onepay.mail.provider=none, défaut) : l'e-mail n'est pas envoyé.
  Le mot de passe temporaire n'est jamais journalisé : le réinitialiser depuis la console Keycloak*/
@Slf4j
@Component
@ConditionalOnProperty(name = "onepay.mail.provider", havingValue = "none", matchIfMissing = true)
public class NoopAccountMailer implements AccountMailer {

    @Override
    public void sendInvitation(AccountInvitation invitation) {
        log.warn("Mail provider not configured: credentials e-mail not sent to user {}", invitation.username());
    }
}
