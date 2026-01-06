package fittoring.config.websocket;

import fittoring.application.auth.service.JwtExtractor;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.auth.service.TokenPayload;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.InvalidTokenException;
import fittoring.config.auth.LoginInfo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@RequiredArgsConstructor
@Component
public class WebSocketAuthHandshakeInterceptor implements HandshakeInterceptor {

    public static final String LOGIN_INFO_KEY = "loginInfo";
    private static final String TOKEN_NAME = "accessToken";

    private final JwtProvider jwtProvider;
    private final JwtExtractor jwtExtractor;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            Cookie[] cookies = httpServletRequest.getCookies();
            validateCookie(cookies);
            String token = jwtExtractor.extractTokenFromCookie(TOKEN_NAME, cookies);
            jwtProvider.validateToken(token);
            TokenPayload payload = jwtProvider.extractTokenPayload(token);
            attributes.put(LOGIN_INFO_KEY, new LoginInfo(payload.sub()));
        }
        return true;
    }

    private void validateCookie(final Cookie[] cookies) {
        if (cookies == null || cookies.length == 0) {
            throw new InvalidTokenException(BusinessErrorMessage.INVALID_TOKEN.getMessage());
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
    }
}
