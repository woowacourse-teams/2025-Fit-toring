package fittoring.application.auth;

import org.springframework.http.ResponseCookie;

public class CookieProvider {

    private static ResponseCookie.ResponseCookieBuilder baseBuilder(String name, String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None");
    }

    public static ResponseCookie createCookie(final String name, final String value) {
        return baseBuilder(name, value)
                .build();
    }

    public static ResponseCookie clearCookie(final String name) {
        final int maxAgeSeconds = 0;
        return baseBuilder(name, "")
                .maxAge(maxAgeSeconds)
                .build();
    }
}
