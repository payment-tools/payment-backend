package com.sn.onepay.notification;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NoopPushSenderTest {

    @Test
    void send_pushesNothingAndRejectsNoToken() {
        assertThat(new NoopPushSender().send(List.of("fcm-1"), new PushMessage("t", "b", Map.of()))).isEmpty();
    }
}
