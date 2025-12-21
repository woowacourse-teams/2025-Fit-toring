package fittoring.application.notification.presentation.dto.request;

public record FcmTokenUpsertRequest(
        Long memberId,
        String token
) {
}
