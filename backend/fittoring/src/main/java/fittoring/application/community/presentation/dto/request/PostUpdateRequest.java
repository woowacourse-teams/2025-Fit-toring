package fittoring.application.community.presentation.dto.request;

public record PostUpdateRequest(
        String title,
        String content,
        String guestPassword
) {
}
