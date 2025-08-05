package fittoring.config.auth;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.service.JwtExtractor;
import fittoring.mentoring.business.service.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;
    private final JwtExtractor jwtExtractor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            responseUnauthorized(response, BusinessErrorMessage.EMPTY_COOKIE.getMessage());
            return false;
        }

        try {
            String accessToken = jwtExtractor.extractTokenFromCookie("accessToken", cookies);
            jwtProvider.validateToken(accessToken);
            Long memberId = jwtProvider.getSubjectFromPayloadBy(accessToken);
            String requestAttributeName = "memberId";
            request.setAttribute(requestAttributeName, memberId);
        } catch (Exception e) {
            responseUnauthorized(response, BusinessErrorMessage.INVALID_TOKEN.getMessage());
            return false;
        }

        return true;
    }

    private void responseUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}

