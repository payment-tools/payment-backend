package com.sn.onepay.notification;

import com.google.firebase.FirebaseApp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPairGenerator;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FirebaseConfigTest {

    @AfterEach
    void deleteApps() {
        FirebaseApp.getApps().forEach(FirebaseApp::delete);
    }

    @Test
    void firebaseMessaging_requiresCredentials() {
        assertThatThrownBy(() -> new FirebaseConfig().firebaseMessaging(" "))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("PUSH_PROVIDER=firebase mais FIREBASE_CREDENTIALS (clé de compte de service JSON) est vide");
        assertThatThrownBy(() -> new FirebaseConfig().firebaseMessaging(null)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void firebaseMessaging_isBuiltFromServiceAccountJson_andReusesTheApp() throws Exception {
        String credentials = fakeServiceAccount();

        var first = new FirebaseConfig().firebaseMessaging(credentials);
        var second = new FirebaseConfig().firebaseMessaging(credentials);

        assertThat(first).isNotNull();
        assertThat(second).isSameAs(first);
        assertThat(FirebaseApp.getApps()).extracting(FirebaseApp::getName).containsExactly("onepay");
    }

    /*Clé de compte de service factice (clé RSA générée à la volée) : aucun appel réseau à l'initialisation*/
    private static String fakeServiceAccount() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        String key = Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(generator.generateKeyPair().getPrivate().getEncoded());
        String pem = "-----BEGIN PRIVATE KEY-----\n" + key + "\n-----END PRIVATE KEY-----\n";
        return """
                {"type": "service_account", "project_id": "onepay-test", "private_key_id": "test",
                 "private_key": "%s", "client_email": "push@onepay-test.iam.gserviceaccount.com", "client_id": "1",
                 "token_uri": "https://oauth2.googleapis.com/token"}
                """.formatted(pem.replace("\n", "\\n"));
    }
}
