package fittoring.application.auth.service.dto;

public record AuthTokenDto(
    String accessToken,
    String refreshToken,
    String oauthSignUpToken
) {

    public AuthTokenDto updateSignUpToken(String token){
        return new AuthTokenDto(accessToken, refreshToken, token);
    }

    public boolean isLoginSuccess(){
        return accessToken != null && refreshToken != null;
    }
}
