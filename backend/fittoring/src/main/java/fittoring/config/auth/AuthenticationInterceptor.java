package fittoring.config.auth;

import fittoring.application.auth.service.JwtExtractor;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.UnAuthorizedException;
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
        return attemptAuthentication(request);
    }

    private boolean isAuthenticationNotRequired(final Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        AuthRequired authRequired = handlerMethod.getMethodAnnotation(AuthRequired.class);
        return authRequired == null;
    }

    private boolean attemptAuthentication(HttpServletRequest request) {
        Cookie[] cookies = getCookies(request);
        String accessToken = getAccessToken(cookies);

        Long memberId = jwtProvider.getSubjectFromPayloadBy(accessToken);
        request.setAttribute("memberId", memberId);
        return true;
    }

    private Cookie[] getCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            throw new UnAuthorizedException(BusinessErrorMessage.EMPTY_COOKIE.getMessage());
        }
        return cookies;
    }

    private String getAccessToken(Cookie[] cookies) {
        return jwtExtractor.extractTokenFromCookie("accessToken", cookies);
    }
}

