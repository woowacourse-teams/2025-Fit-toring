package fittoring.application.notification.presentation.dto.request;

public record PushTokenUpsertRequest(
        Long memberId,
        String hardwareId,
        String pushToken
) {
}
