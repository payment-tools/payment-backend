package com.sn.onepay.mail;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class NoopAccountMailerTest {

    @Test
    void sendInvitation_doesNotFail() {
        assertThatCode(() -> new NoopAccountMailer()
                .sendInvitation(new AccountInvitation("jean@mail.com", "Jean", "Dupont", "jdupont", "Tmp-Pass-123")))
                .doesNotThrowAnyException();
    }
}
