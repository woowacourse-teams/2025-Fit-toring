package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.InvalidTokenException;
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
                .orElseThrow(() -> new InvalidTokenException(BusinessErrorMessage.NOT_FOUND_TOKEN.getMessage()));
    }
}
