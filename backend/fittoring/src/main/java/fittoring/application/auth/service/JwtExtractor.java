package fittoring.application.auth.service;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.InvalidTokenException;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import org.springframework.stereotype.Component;

@Component
public class JwtExtractor {

    public String extractTokenFromCookie(String name, Cookie[] cookies) {
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(name))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new InvalidTokenException(BusinessErrorMessage.TOKEN_NOT_FOUND.getMessage()));
    }
}
