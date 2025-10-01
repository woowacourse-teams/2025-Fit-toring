package fittoring.mentoring.presentation.dto;

public record LoginResponse(
        Long memberId,
        AuthTokenResponse authToken
) {
}
