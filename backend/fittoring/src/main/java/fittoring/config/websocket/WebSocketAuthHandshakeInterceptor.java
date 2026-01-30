package fittoring.config.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import fittoring.application.auth.service.JwtExtractor;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.auth.service.TokenPayload;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.InvalidTokenException;
import fittoring.application.exception.UnauthorizedException;
import fittoring.config.auth.LoginInfo;
import fittoring.exception.ErrorResponse;
import fittoring.exception.SystemErrorMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketAuthHandshakeInterceptor implements HandshakeInterceptor {

    public static final String LOGIN_INFO_KEY = "loginInfo";
    private static final String TOKEN_NAME = "accessToken";

    private final JwtProvider jwtProvider;
    private final JwtExtractor jwtExtractor;
    private final ObjectMapper objectMapper;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        try {
            if (request instanceof ServletServerHttpRequest servletRequest) {
                HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
                Cookie[] cookies = httpServletRequest.getCookies();
                validateCookie(cookies);
                String token = jwtExtractor.extractTokenFromCookie(TOKEN_NAME, cookies);
                TokenPayload payload = jwtProvider.extractTokenPayload(token);
                attributes.put(LOGIN_INFO_KEY, new LoginInfo(payload.sub()));
            }
            return true;
        } catch (UnauthorizedException | InvalidTokenException e) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, e.getMessage());
            return false;
        } catch (Exception e) {
            writeErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR,
                    SystemErrorMessage.INTERNAL_SERVER_ERROR.getMessage());
            return false;
        }
    }

    private void validateCookie(final Cookie[] cookies) {
        log.info("sockJS 쿠키 검증");
        if (cookies == null || cookies.length == 0) {
            throw new UnauthorizedException(BusinessErrorMessage.EMPTY_COOKIE.getMessage());
        }
    }

    private void writeErrorResponse(ServerHttpResponse response, HttpStatus status, String message) {
        response.setStatusCode(status);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse body = ErrorResponse.of(status, message);
        try {
            byte[] bytes = objectMapper.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
            response.getBody().write(bytes);
        } catch (IOException ignore) {
            // 원래 실패 원인을 덮어쓰지 않기 위해서 비워둠
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
