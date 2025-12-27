package fittoring.infrastructure.dto;

public record FcmNotificationRequest(
        String fcmToken,
        String title,
        String body,
        Long memberId
) {
}
