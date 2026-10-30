package com.sn.onepay.notification;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class FirebasePushSenderTest {

    final FirebaseMessaging firebaseMessaging = mock(FirebaseMessaging.class);
    final FirebasePushSender sender = new FirebasePushSender(firebaseMessaging);
    final PushMessage message = new PushMessage("Paiement reçu", "Paiement reçu : 2 500 FCFA", Map.of("paymentId", "7"));

    @Test
    void send_returnsOnlyTheTokensRejectedForGood() throws FirebaseMessagingException {
        List<SendResponse> responses = List.of(ok(), failed(MessagingErrorCode.UNREGISTERED), failed(MessagingErrorCode.INVALID_ARGUMENT), failed(MessagingErrorCode.UNAVAILABLE), failed(null));
        BatchResponse batch = mock(BatchResponse.class);
        when(batch.getResponses()).thenReturn(responses);
        when(batch.getSuccessCount()).thenReturn(1);
        when(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class))).thenReturn(batch);

        List<String> stale = sender.send(List.of("ok", "unregistered", "invalid", "temporary", "no-error-code"), message);

        assertThat(stale).containsExactly("unregistered", "invalid");
    }

    @Test
    void send_failureOfTheWholeBatchRemovesNoToken() throws FirebaseMessagingException {
        FirebaseMessagingException error = mock(FirebaseMessagingException.class);
        when(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class))).thenThrow(error);

        assertThat(sender.send(List.of("fcm-1"), message)).isEmpty();
    }

    @Test
    void send_withoutTokenCallsNothing() {
        assertThat(sender.send(List.of(), message)).isEmpty();
        verifyNoInteractions(firebaseMessaging);
    }

    private static SendResponse ok() {
        SendResponse response = mock(SendResponse.class);
        when(response.getException()).thenReturn(null);
        return response;
    }

    private static SendResponse failed(MessagingErrorCode code) {
        FirebaseMessagingException exception = mock(FirebaseMessagingException.class);
        when(exception.getMessagingErrorCode()).thenReturn(code);
        SendResponse response = mock(SendResponse.class);
        when(response.getException()).thenReturn(exception);
        return response;
    }
}
