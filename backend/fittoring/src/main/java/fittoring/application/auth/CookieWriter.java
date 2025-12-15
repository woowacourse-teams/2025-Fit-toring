package fittoring.application.auth;

import fittoring.application.auth.service.dto.AuthTokenDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CookieWriter {

    private final CookieProvider cookieProvider;

    public void write(HttpServletResponse response, AuthTokenDto tokens) {
        ResponseCookie accessToken = cookieProvider.createCookie("accessToken", tokens.accessToken());
        ResponseCookie refreshToken = cookieProvider.createCookie("refreshToken", tokens.refreshToken());
        response.addHeader(HttpHeaders.SET_COOKIE, accessToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshToken.toString());
    }

    public void writeOauthSignUpToken(HttpServletResponse response, String oauthSignUpToken) {
        ResponseCookie cookie = cookieProvider.createCookie("oauthSignUpToken", oauthSignUpToken);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clearCookies(HttpServletResponse response) {
        ResponseCookie accessToken = cookieProvider.clearCookie("accessToken");
        ResponseCookie refreshToken = cookieProvider.clearCookie("refreshToken");
        ResponseCookie oauthSignUpToken = cookieProvider.clearCookie("oauthSignUpToken");

        response.addHeader(HttpHeaders.SET_COOKIE, accessToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, oauthSignUpToken.toString());
    }
}
