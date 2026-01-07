package fittoring.config.auth;

import fittoring.application.auth.service.JwtExtractor;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.auth.service.TokenPayload;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;
    private final JwtExtractor jwtExtractor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            return true;
        }
        if (isAuthenticationNotRequired(handler)) {
            return true;
        }
        return attemptAuthentication(request, handler);
    }

    private boolean isAuthenticationNotRequired(final Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        boolean hasAuthRequired = handlerMethod.hasMethodAnnotation(AuthRequired.class);
        boolean hasAdmin = handlerMethod.hasMethodAnnotation(Admin.class);
        return !hasAuthRequired && !hasAdmin;
    }

    private boolean attemptAuthentication(HttpServletRequest request, Object handler) {
        Cookie[] cookies = getCookies(request);
        String accessToken = getAccessToken(cookies);

        TokenPayload payload = jwtProvider.extractTokenPayload(accessToken);
        validateAdminRole(handler, payload.role());
        request.setAttribute("memberId", payload.sub());
        return true;
    }

    private Cookie[] getCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            throw new UnauthorizedException(BusinessErrorMessage.EMPTY_COOKIE.getMessage());
        }
        return cookies;
    }

    private String getAccessToken(Cookie[] cookies) {
        return jwtExtractor.extractTokenFromCookie("accessToken", cookies);
    }

    private void validateAdminRole(Object handler, String role) {
        if (handler instanceof HandlerMethod handlerMethod) {
            if (isNotAdmin(role, handlerMethod)) {
                throw new ForbiddenException(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
            }
        }
    }

    private boolean isNotAdmin(String role, HandlerMethod handlerMethod) {
        return handlerMethod.hasMethodAnnotation(Admin.class) && !"ADMIN".equals(role);
    }
}
