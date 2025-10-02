package fittoring.application.business.service;

import fittoring.application.business.exception.BusinessErrorMessage;
import fittoring.application.business.exception.InvalidTokenException;
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
