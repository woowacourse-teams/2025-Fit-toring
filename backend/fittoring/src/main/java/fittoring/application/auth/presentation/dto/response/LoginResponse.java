package fittoring.application.auth.presentation.dto.response;

import fittoring.application.mentoring.presentation.dto.response.MemberLoginResponse;

public record LoginResponse(
        MemberLoginResponse memberLoginResponse,
        AuthTokenResponse authToken
) {
}
