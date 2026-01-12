package fittoring.application.notification.presentation.dto.request;

public record RegisterDeviceRequest(
        Long memberId,
        String pushToken
) {
}
