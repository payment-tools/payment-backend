package com.sn.onepay.notification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/*Client FCM, initialisé avec la clé de compte de service Firebase (JSON) fournie par FIREBASE_CREDENTIALS*/
@Configuration
@ConditionalOnProperty(name = "onepay.push.provider", havingValue = "firebase")
public class FirebaseConfig {

    static final String APP_NAME = "onepay";

    @Bean
    public FirebaseMessaging firebaseMessaging(@Value("${onepay.push.firebase.credentials:}") String credentials) throws IOException {
        if (credentials == null || credentials.isBlank()) {
            throw new IllegalStateException("PUSH_PROVIDER=firebase mais FIREBASE_CREDENTIALS (clé de compte de service JSON) est vide");
        }
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ByteArrayInputStream(credentials.getBytes(StandardCharsets.UTF_8)));
        FirebaseApp app = FirebaseApp.getApps().stream()
                .filter(existing -> APP_NAME.equals(existing.getName()))
                .findFirst()
                .orElseGet(() -> FirebaseApp.initializeApp(FirebaseOptions.builder().setCredentials(googleCredentials).build(), APP_NAME));
        return FirebaseMessaging.getInstance(app);
    }
}
