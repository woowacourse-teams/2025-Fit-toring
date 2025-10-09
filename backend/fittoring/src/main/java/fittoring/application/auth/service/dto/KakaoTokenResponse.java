package fittoring.application.auth.service.dto;

public record KakaoTokenResponse(
        String access_token,
        Integer expires_in,
        String refresh_token,
        Integer refresh_token_expires_in
) {
}
