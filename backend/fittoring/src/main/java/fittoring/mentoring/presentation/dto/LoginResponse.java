package fittoring.mentoring.presentation.dto;

public record LoginResponse(
        MemberLoginResponse memberLoginResponse,
        AuthTokenResponse authToken
) {
}
