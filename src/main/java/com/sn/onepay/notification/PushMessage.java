package com.sn.onepay.notification;

import java.util.Map;

public record PushMessage(String title, String body, Map<String, String> data) {
}
