package fittoring.mentoring.presentation;

import fittoring.mentoring.presentation.dto.AuthTokenResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieWriter {

    public static void write(HttpServletResponse response, AuthTokenResponse tokens) {
        ResponseCookie accessToken = CookieProvider.createCookie("accessToken", tokens.accessToken());
        ResponseCookie refreshToken = CookieProvider.createCookie("refreshToken", tokens.refreshToken());

        response.addHeader(HttpHeaders.SET_COOKIE, accessToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshToken.toString());
    }
}


