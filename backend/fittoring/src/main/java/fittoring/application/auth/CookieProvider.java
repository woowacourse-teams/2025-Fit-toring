package fittoring.application.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieProvider {

    private final String sameSite;

    public CookieProvider(@Value("${cookie.same-site}") String sameSite) {
        this.sameSite = sameSite;
    }

    private ResponseCookie.ResponseCookieBuilder baseBuilder(final String name, final String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite(sameSite);
    }

    public ResponseCookie createCookie(final String name, final String value) {
        return baseBuilder(name, value)
                .build();
    }

    public ResponseCookie clearCookie(final String name) {
        final int maxAgeSeconds = 0;
        return baseBuilder(name, "")
                .maxAge(maxAgeSeconds)
                .build();
    }
}
