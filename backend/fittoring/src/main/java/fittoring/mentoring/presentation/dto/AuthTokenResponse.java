package fittoring.mentoring.presentation.dto;

public record AuthTokenResponse(
        String accessToken,
        String refreshToken
) {
}
