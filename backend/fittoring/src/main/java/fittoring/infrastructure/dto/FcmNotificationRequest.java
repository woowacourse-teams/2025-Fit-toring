package fittoring.infrastructure.dto;

import java.util.Map;

public record FcmNotificationRequest(
        String fcmToken,
        Map<String, String> data
) {
}
