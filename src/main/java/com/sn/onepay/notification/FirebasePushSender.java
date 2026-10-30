package com.sn.onepay.notification;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@ConditionalOnProperty(name = "onepay.push.provider", havingValue = "firebase")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FirebasePushSender implements PushSender {

    /*Jeton définitivement inutilisable : l'appareil s'est désinscrit ou le jeton est mal formé*/
    static final Set<MessagingErrorCode> STALE_TOKEN_ERRORS = Set.of(MessagingErrorCode.UNREGISTERED, MessagingErrorCode.INVALID_ARGUMENT);

    final FirebaseMessaging firebaseMessaging;

    @Override
    public List<String> send(List<String> tokens, PushMessage message) {
        if (tokens.isEmpty()) {
            return List.of();
        }
        MulticastMessage multicast = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder().setTitle(message.title()).setBody(message.body()).build())
                .putAllData(message.data())
                .build();
        try {
            BatchResponse response = firebaseMessaging.sendEachForMulticast(multicast);
            List<String> staleTokens = new ArrayList<>();
            List<SendResponse> responses = response.getResponses();
            for (int i = 0; i < responses.size(); i++) {
                FirebaseMessagingException error = responses.get(i).getException();
                // Code d'erreur absent (ex. échec d'authentification auprès de Google) : jeton conservé
                if (error != null && error.getMessagingErrorCode() != null && STALE_TOKEN_ERRORS.contains(error.getMessagingErrorCode())) {
                    staleTokens.add(tokens.get(i));
                }
            }
            log.info("Push sent to {}/{} device(s)", response.getSuccessCount(), tokens.size());
            return staleTokens;
        } catch (FirebaseMessagingException exception) {
            log.error("Push sending failed: {}", exception.getMessage());
            return List.of();
        }
    }
}
