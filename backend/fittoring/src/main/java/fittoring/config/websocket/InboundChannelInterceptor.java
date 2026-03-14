package fittoring.config.websocket;

import fittoring.application.auth.service.JwtProvider;
import fittoring.application.auth.service.TokenPayload;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ExpiredTokenException;
import fittoring.application.exception.UnauthorizedException;
import fittoring.config.auth.LoginInfo;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class InboundChannelInterceptor implements ChannelInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String ACCESS_TOKEN_HEADER = "accessToken";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;
    private final WebSocketMetricsListener metricsListener;

    /**
     * STOMP 메시지 처리를 위한 인터셉터 - 인증 정보 전달 - 메트릭 수집 (처리량, 에러율)
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();
        if (StompCommand.CONNECT.equals(command)) {
            authenticate(accessor);
            return message;
        }

        if (StompCommand.SEND.equals(command) || StompCommand.SUBSCRIBE.equals(command)) {
            validateAuthenticated(accessor);
            validateSessionExpired(accessor);
        }

        if (StompCommand.SEND.equals(command)) {
            LoginInfo loginInfo = (LoginInfo) getSessionAttributes(accessor)
                    .get(WebSocketAuthHandshakeInterceptor.LOGIN_INFO_KEY);
            accessor.setHeader(WebSocketAuthHandshakeInterceptor.LOGIN_INFO_KEY, loginInfo);
            metricsListener.incrementInboundMessage(resolvePayloadSize(message.getPayload()));

            return MessageBuilder.createMessage(
                    message.getPayload(),
                    accessor.getMessageHeaders()
            );
        }
        return message;
    }

    private void authenticate(StompHeaderAccessor accessor) {
        String accessToken = resolveAccessToken(accessor);
        TokenPayload payload = jwtProvider.extractTokenPayload(accessToken);
        long expEpochMillis = jwtProvider.extractExpirationMillis(accessToken);

        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        sessionAttributes.put(WebSocketAuthHandshakeInterceptor.LOGIN_INFO_KEY, new LoginInfo(payload.sub()));
        sessionAttributes.put(WebSocketAuthHandshakeInterceptor.TOKEN_EXP_EPOCH_MILLIS_KEY, expEpochMillis);
    }

    private String resolveAccessToken(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);
        if (authorization != null) {
            return extractBearerToken(authorization);
        }

        String accessToken = accessor.getFirstNativeHeader(ACCESS_TOKEN_HEADER);
        if (accessToken != null && !accessToken.isBlank()) {
            return accessToken;
        }

        Object token = getSessionAttributes(accessor).get(WebSocketAuthHandshakeInterceptor.ACCESS_TOKEN_KEY);
        if (token instanceof String text && !text.isBlank()) {
            return text;
        }

        throw new UnauthorizedException(BusinessErrorMessage.TOKEN_NOT_FOUND.getMessage());
    }

    private String extractBearerToken(String authorization) {
        if (authorization.isBlank()) {
            throw new UnauthorizedException(BusinessErrorMessage.EMPTY_TOKEN.getMessage());
        }
        if (!authorization.startsWith(BEARER_PREFIX)) {
            return authorization;
        }

        String token = authorization.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            throw new UnauthorizedException(BusinessErrorMessage.EMPTY_TOKEN.getMessage());
        }
        return token;
    }

    private void validateAuthenticated(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        if (!sessionAttributes.containsKey(WebSocketAuthHandshakeInterceptor.LOGIN_INFO_KEY)
                || !sessionAttributes.containsKey(WebSocketAuthHandshakeInterceptor.TOKEN_EXP_EPOCH_MILLIS_KEY)) {
            throw new UnauthorizedException(BusinessErrorMessage.TOKEN_NOT_FOUND.getMessage());
        }
    }

    private void validateSessionExpired(StompHeaderAccessor accessor) {
        long expEpochMillis = getExpEpochMillis(accessor);
        validateTokenExpired(expEpochMillis);
    }

    private long getExpEpochMillis(StompHeaderAccessor accessor) {
        return (long) getSessionAttributes(accessor)
                .get(WebSocketAuthHandshakeInterceptor.TOKEN_EXP_EPOCH_MILLIS_KEY);
    }

    private Map<String, Object> getSessionAttributes(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes == null) {
            throw new UnauthorizedException(BusinessErrorMessage.TOKEN_NOT_FOUND.getMessage());
        }
        return sessionAttributes;
    }

    private void validateTokenExpired(long expEpochMillis) {
        if (isTokenExpired(expEpochMillis)) {
            throw new ExpiredTokenException(BusinessErrorMessage.EXPIRED_TOKEN.getMessage());
        }
    }

    private boolean isTokenExpired(long expEpochMillis) {
        return Instant.now().toEpochMilli() > expEpochMillis;
    }

    /**
     * 메시지 전송 완료 후 처리 - 에러 카운트
     */
    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, @Nullable Exception ex) {
        if (ex != null) {
            metricsListener.incrementError(ex);
            log.error("WebSocket message processing failed", ex);
        }
    }

    private int resolvePayloadSize(Object payload) {
        if (payload == null) {
            return 0;
        }
        if (payload instanceof byte[] bytes) {
            return bytes.length;
        }
        if (payload instanceof String text) {
            return text.getBytes(StandardCharsets.UTF_8).length;
        }
        return payload.toString().getBytes(StandardCharsets.UTF_8).length;
    }
}
