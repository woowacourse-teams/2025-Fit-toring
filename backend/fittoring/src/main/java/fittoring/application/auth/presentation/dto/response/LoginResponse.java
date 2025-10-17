package fittoring.application.auth.presentation.dto.response;

public record LoginResponse(
        Long memberId,
        AuthTokenResponse authToken
) {
}
