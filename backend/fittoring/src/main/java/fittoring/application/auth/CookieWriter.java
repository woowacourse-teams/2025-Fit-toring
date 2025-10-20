package fittoring.application.auth;

import fittoring.application.auth.presentation.dto.response.AuthTokenDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieWriter {

    public static void write(HttpServletResponse response, AuthTokenDto tokens) {
        ResponseCookie accessToken = CookieProvider.createCookie("accessToken", tokens.accessToken());
        ResponseCookie refreshToken = CookieProvider.createCookie("refreshToken", tokens.refreshToken());
        response.addHeader(HttpHeaders.SET_COOKIE, accessToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshToken.toString());
    }

    public static void clearCookies(HttpServletResponse response) {
        ResponseCookie accessToken = CookieProvider.clearCookie("accessToken");
        ResponseCookie refreshToken = CookieProvider.clearCookie("refreshToken");
        ResponseCookie oauthSignUpToken = CookieProvider.clearCookie("oauthSignUpToken");

        response.addHeader(HttpHeaders.SET_COOKIE, accessToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, oauthSignUpToken.toString());
    }
}


