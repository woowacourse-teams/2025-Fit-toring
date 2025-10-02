package fittoring.application.auth.presentation.dto.response;

public record AuthTokenResponse(
    String accessToken,
    String refreshToken,
    String oauthSignUpToken
) {

    public AuthTokenResponse updateSignUpToken(String token){
        return new AuthTokenResponse(accessToken, refreshToken, token);
    }

    public boolean isLoginSuccess(){
        return accessToken != null && refreshToken != null;
    }
}
