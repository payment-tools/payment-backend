package com.sn.onepay.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Map;

/*Envoi par l'API HTTP transactionnelle de Brevo (et non SMTP : ports SMTP bloqués sur Render gratuit)*/
@Slf4j
@Component
@ConditionalOnProperty(name = "onepay.mail.provider", havingValue = "brevo")
public class BrevoAccountMailer implements AccountMailer {

    static final String SUBJECT = "Votre compte OnePay+";

    private final RestClient restClient;
    private final String apiKey;
    private final String fromEmail;
    private final String fromName;
    private final String loginUrl;

    public BrevoAccountMailer(RestClient.Builder restClientBuilder,
                              @Value("${onepay.mail.brevo.api-url}") String apiUrl,
                              @Value("${onepay.mail.brevo.api-key}") String apiKey,
                              @Value("${onepay.mail.from-email}") String fromEmail,
                              @Value("${onepay.mail.from-name}") String fromName,
                              @Value("${onepay.mail.login-url}") String loginUrl) {
        this.restClient = restClientBuilder.baseUrl(apiUrl).build();
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.loginUrl = loginUrl;
    }

    @Override
    public void sendInvitation(AccountInvitation invitation) {
        String recipientName = ((invitation.firstname() == null ? "" : invitation.firstname()) + " "
                + (invitation.lastname() == null ? "" : invitation.lastname())).trim();
        Map<String, Object> to = recipientName.isEmpty()
                ? Map.of("email", invitation.email())
                : Map.of("email", invitation.email(), "name", recipientName);
        restClient.post()
                .uri("/v3/smtp/email")
                .header("api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "sender", Map.of("email", fromEmail, "name", fromName),
                        "to", List.of(to),
                        "subject", SUBJECT,
                        "textContent", textContent(invitation, recipientName),
                        "htmlContent", htmlContent(invitation, recipientName)))
                .retrieve()
                .toBodilessEntity();
        log.info("Credentials e-mail sent to user {}", invitation.username());
    }

    String textContent(AccountInvitation invitation, String recipientName) {
        return "Bonjour " + (recipientName.isEmpty() ? "" : recipientName) + ",\n\n"
                + "Un compte OnePay+ vient d'être créé pour vous.\n\n"
                + "Identifiant : " + invitation.username() + "\n"
                + "Mot de passe temporaire : " + invitation.temporaryPassword() + "\n\n"
                + "Connectez-vous sur " + loginUrl + " ou dans l'application mobile OnePay+ : "
                + "il vous sera demandé de choisir votre mot de passe définitif dès la première connexion.\n\n"
                + "Si vous n'êtes pas à l'origine de cette demande, ignorez ce message.\n\n"
                + "L'équipe OnePay+";
    }

    String htmlContent(AccountInvitation invitation, String recipientName) {
        return "<p>Bonjour " + HtmlUtils.htmlEscape(recipientName) + ",</p>"
                + "<p>Un compte OnePay+ vient d'être créé pour vous.</p>"
                + "<p>Identifiant : <strong>" + HtmlUtils.htmlEscape(invitation.username()) + "</strong><br>"
                + "Mot de passe temporaire : <strong><code>" + HtmlUtils.htmlEscape(invitation.temporaryPassword()) + "</code></strong></p>"
                + "<p>Connectez-vous sur <a href=\"" + HtmlUtils.htmlEscape(loginUrl) + "\">" + HtmlUtils.htmlEscape(loginUrl) + "</a>"
                + " ou dans l'application mobile OnePay+ : il vous sera demandé de choisir votre mot de passe définitif dès la première connexion.</p>"
                + "<p>Si vous n'êtes pas à l'origine de cette demande, ignorez ce message.</p>"
                + "<p>L'équipe OnePay+</p>";
    }
}
