package com.sn.onepay.mail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.hamcrest.Matchers.containsString;

class BrevoAccountMailerTest {

    static final AccountInvitation INVITATION =
            new AccountInvitation("jean@mail.com", "Jean", "Dupont", "jdupont", "Tmp-Pass-123");

    MockRestServiceServer server;
    BrevoAccountMailer mailer;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        mailer = new BrevoAccountMailer(builder, "https://brevo", "api-key-123", "no-reply@onepay.sn", "OnePay+", "https://app.onepay.sn");
    }

    @Test
    void sendInvitation_postsTransactionalEmailWithCredentials() {
        server.expect(requestTo("https://brevo/v3/smtp/email"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("api-key", "api-key-123"))
                .andExpect(jsonPath("$.sender.email").value("no-reply@onepay.sn"))
                .andExpect(jsonPath("$.sender.name").value("OnePay+"))
                .andExpect(jsonPath("$.to[0].email").value("jean@mail.com"))
                .andExpect(jsonPath("$.to[0].name").value("Jean Dupont"))
                .andExpect(jsonPath("$.subject").value(BrevoAccountMailer.SUBJECT))
                .andExpect(jsonPath("$.textContent", containsString("Identifiant : jdupont")))
                .andExpect(jsonPath("$.textContent", containsString("Mot de passe temporaire : Tmp-Pass-123")))
                .andExpect(jsonPath("$.htmlContent", containsString("https://app.onepay.sn")))
                .andRespond(withStatus(HttpStatus.CREATED));

        mailer.sendInvitation(INVITATION);
        server.verify();
    }

    @Test
    void sendInvitation_withoutNames_omitsRecipientName() {
        server.expect(requestTo("https://brevo/v3/smtp/email"))
                .andExpect(jsonPath("$.to[0].email").value("jean@mail.com"))
                .andExpect(jsonPath("$.to[0].name").doesNotExist())
                .andRespond(withStatus(HttpStatus.CREATED));

        mailer.sendInvitation(new AccountInvitation("jean@mail.com", null, null, "jdupont", "Tmp-Pass-123"));
        server.verify();
    }

    @Test
    void sendInvitation_apiError_throws() {
        server.expect(requestTo("https://brevo/v3/smtp/email")).andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        assertThatThrownBy(() -> mailer.sendInvitation(INVITATION)).isInstanceOf(HttpClientErrorException.class);
    }

    @Test
    void htmlContent_escapesUserProvidedValues() {
        String html = mailer.htmlContent(new AccountInvitation("x@mail.com", "<b>", null, "a&b", "p<w>"), "<script>");

        assertThat(html).doesNotContain("<script>").contains("&lt;script&gt;").contains("a&amp;b").contains("p&lt;w&gt;");
    }
}
