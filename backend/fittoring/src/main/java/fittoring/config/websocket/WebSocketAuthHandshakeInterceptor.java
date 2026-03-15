package fittoring.config.websocket;

import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
public class WebSocketAuthHandshakeInterceptor implements HandshakeInterceptor {

    public static final String LOGIN_INFO_KEY = "loginInfo";
    public static final String TOKEN_EXP_EPOCH_MILLIS_KEY = "tokenExpEpochMillis";
    public static final String ACCESS_TOKEN_KEY = "accessToken";
    private static final String TOKEN_NAME = "accessToken";

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            Cookie[] cookies = servletRequest.getServletRequest().getCookies();
            extractToken(cookies).ifPresent(token -> attributes.put(ACCESS_TOKEN_KEY, token));
        }
        return true;
    }

    private Optional<String> extractToken(Cookie[] cookies) {
        if (cookies == null || cookies.length == 0) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> TOKEN_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
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
