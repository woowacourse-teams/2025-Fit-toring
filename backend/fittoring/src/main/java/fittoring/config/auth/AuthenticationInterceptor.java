package fittoring.config.auth;

import fittoring.application.business.exception.BusinessErrorMessage;
import fittoring.application.business.service.JwtExtractor;
import fittoring.application.business.service.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;
    private final JwtExtractor jwtExtractor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            return true;
        }
        if (isAuthenticationNotRequired(handler)) {
            return true;
        }
        return attemptAuthentication(request, response);
    }

    private boolean isAuthenticationNotRequired(final Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        AuthRequired authRequired = handlerMethod.getMethodAnnotation(AuthRequired.class);
        return authRequired == null;
    }

    private boolean attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            respondUnauthorized(response, BusinessErrorMessage.EMPTY_COOKIE.getMessage());
            return false;
        }
        try {
            String accessToken = jwtExtractor.extractTokenFromCookie("accessToken", cookies);
            jwtProvider.validateToken(accessToken);
            Long memberId = jwtProvider.getSubjectFromPayloadBy(accessToken);
            String requestAttributeName = "memberId";
            request.setAttribute(requestAttributeName, memberId);
        } catch (Exception e) {
            respondUnauthorized(response, BusinessErrorMessage.INVALID_TOKEN.getMessage());
            return false;
        }
        return true;
    }

    private void respondUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}

