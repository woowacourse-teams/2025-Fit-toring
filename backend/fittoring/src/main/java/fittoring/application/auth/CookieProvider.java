package fittoring.application.auth;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;

public class CookieProvider {

    public static ResponseCookie createCookie(final String name, final String value) {
        return baseBuilder(name, value)
                .build();
    }

    public static ResponseCookie createCookieWithMaxAge(final String name, final String value) {
        final long maxAgeSeconds = 604800L;
        return baseBuilder(name, value)
                .maxAge(maxAgeSeconds)
                .build();
    }

    public static ResponseCookie clearCookie(final String name) {
        final long maxAgeSeconds = 0L;
        return baseBuilder(name, "")
                .maxAge(maxAgeSeconds)
                .build();
    }

    private static ResponseCookieBuilder baseBuilder(String name, String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None");
    }
}
