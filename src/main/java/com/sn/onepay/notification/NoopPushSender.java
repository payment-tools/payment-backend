package com.sn.onepay.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/*Push désactivé (PUSH_PROVIDER=none, défaut local) : la notification reste dans l'historique en base*/
@Slf4j
@Component
@ConditionalOnProperty(name = "onepay.push.provider", havingValue = "none", matchIfMissing = true)
public class NoopPushSender implements PushSender {

    @Override
    public List<String> send(List<String> tokens, PushMessage message) {
        log.warn("Push provider not configured: notification not pushed to {} device(s)", tokens.size());
        return List.of();
    }
}
