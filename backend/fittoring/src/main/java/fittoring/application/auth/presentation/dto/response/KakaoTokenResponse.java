package fittoring.application.auth.presentation.dto.response;

public record KakaoTokenResponse(
        String access_token,
        Integer expires_in,
        String refresh_token,
        Integer refresh_token_expires_in
) {
}
