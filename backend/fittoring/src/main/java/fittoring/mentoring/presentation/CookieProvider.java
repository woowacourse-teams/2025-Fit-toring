package fittoring.mentoring.presentation;

import org.springframework.http.ResponseCookie;

public class CookieProvider {

    public static ResponseCookie createCookie(final String name, final String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .build();
    }
}
