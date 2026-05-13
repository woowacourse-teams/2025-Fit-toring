package fittoring.config.websocket;

import fittoring.application.auth.service.JwtProvider;
import fittoring.application.auth.service.TokenPayload;
import fittoring.application.chat.service.ChatRoomService;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ExpiredTokenException;
import fittoring.application.exception.UnauthorizedChatRoomAccessException;
import fittoring.application.exception.UnauthorizedException;
import fittoring.config.auth.LoginInfo;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class InboundChannelInterceptor implements ChannelInterceptor, ExecutorChannelInterceptor {

    public static final String LOGIN_INFO_KEY = "loginInfo";

    private static final String AUTHORIZED_CHAT_ROOM_IDS_KEY = "authorizedChatRoomIds";
    private static final String INBOUND_ENQUEUED_AT_NS_KEY = "wsInboundEnqueuedAtNs";
    private static final String TOKEN_EXP_EPOCH_MILLIS_KEY = "tokenExpEpochMillis";
    private static final String TOKEN_NAME = "accessToken";
    private static final String TOPIC_CHATROOM_PREFIX = "/topic/chatroom/";
    private static final String APP_CHATROOM_PREFIX = "/app/chatroom/";

    private final JwtProvider jwtProvider;
    private final ChatRoomService chatRoomService;
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

        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        if (StompCommand.SEND.equals(command) || StompCommand.SUBSCRIBE.equals(command)) {
            validateAuthenticated(sessionAttributes);
        }

        if (StompCommand.SUBSCRIBE.equals(command)) {
            authorizeChatRoomSubscriptionIfNeeded(accessor, sessionAttributes);
            return message;
        }

        if (StompCommand.SEND.equals(command)) {
            LoginInfo loginInfo = (LoginInfo) sessionAttributes.get(LOGIN_INFO_KEY);
            validateChatRoomSendAuthorized(accessor, sessionAttributes);
            accessor.setHeader(LOGIN_INFO_KEY, loginInfo);
            accessor.setHeader(INBOUND_ENQUEUED_AT_NS_KEY, System.nanoTime());

            metricsListener.incrementInboundMessage(resolvePayloadSize(message.getPayload()));

            return message;
        }
        return message;
    }

    @Override
    public Message<?> beforeHandle(Message<?> message, MessageChannel channel, MessageHandler handler) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || !StompCommand.SEND.equals(accessor.getCommand())) {
            return message;
        }

        Long enqueuedAtNs = (Long) accessor.getHeader(INBOUND_ENQUEUED_AT_NS_KEY);
        if (enqueuedAtNs != null) {
            metricsListener.recordInboundQueueWait(System.nanoTime() - enqueuedAtNs);
            WebSocketMetricContext.setInboundEnqueuedAtNs(enqueuedAtNs);
        }

        return message;
    }

    @Override
    public void afterMessageHandled(
            Message<?> message,
            MessageChannel channel,
            MessageHandler handler,
            @Nullable Exception ex
    ) {
        WebSocketMetricContext.clear();
    }

    private void authenticate(StompHeaderAccessor accessor) { //토큰 검증
        String accessToken = getTokenFromSession(accessor);
        TokenPayload payload = jwtProvider.extractTokenPayload(accessToken);
        long expEpochMillis = jwtProvider.extractExpirationMillis(accessToken);

        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        storeAuthenticationInSession(sessionAttributes, payload, expEpochMillis);
    }

    private void storeAuthenticationInSession(
            Map<String, Object> sessionAttributes,
            TokenPayload payload,
            long expEpochMillis
    ) {
        sessionAttributes.put(LOGIN_INFO_KEY, new LoginInfo(payload.sub()));
        sessionAttributes.put(TOKEN_EXP_EPOCH_MILLIS_KEY, expEpochMillis);
    }

    private String getTokenFromSession(StompHeaderAccessor accessor) {
        Object token = getSessionAttributes(accessor).get(TOKEN_NAME);
        if (token instanceof String text && !text.isBlank()) {
            return text;
        }

        throw new UnauthorizedException(BusinessErrorMessage.TOKEN_NOT_FOUND.getMessage());
    }

    private void validateAuthenticated(Map<String, Object> sessionAttributes) {
        if (!sessionAttributes.containsKey(LOGIN_INFO_KEY) ||
                !sessionAttributes.containsKey(TOKEN_EXP_EPOCH_MILLIS_KEY)) {
            throw new UnauthorizedException(BusinessErrorMessage.TOKEN_NOT_FOUND.getMessage());
        }
        validateTokenExpired(sessionAttributes);
    }

    private void validateTokenExpired(Map<String, Object> sessionAttributes) {
        long expEpochMillis = getExpEpochMillis(sessionAttributes);
        if (isTokenExpired(expEpochMillis)) {
            throw new ExpiredTokenException(BusinessErrorMessage.EXPIRED_TOKEN.getMessage());
        }
    }

    private long getExpEpochMillis(Map<String, Object> sessionAttributes) {
        return (long) sessionAttributes.get(TOKEN_EXP_EPOCH_MILLIS_KEY);
    }

    private Map<String, Object> getSessionAttributes(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes == null) {
            throw new UnauthorizedException(BusinessErrorMessage.TOKEN_NOT_FOUND.getMessage());
        }
        return sessionAttributes;
    }

    private boolean isTokenExpired(long expEpochMillis) {
        return Instant.now().toEpochMilli() > expEpochMillis;
    }

    private void authorizeChatRoomSubscriptionIfNeeded(
            StompHeaderAccessor accessor,
            Map<String, Object> sessionAttributes
    ) {
        Long chatRoomId = extractChatRoomId(accessor.getDestination(), TOPIC_CHATROOM_PREFIX);
        if (chatRoomId == null) {
            return;
        }

        LoginInfo loginInfo = (LoginInfo) sessionAttributes.get(LOGIN_INFO_KEY);
        chatRoomService.getAccessibleChatRoom(chatRoomId, loginInfo.memberId());
        getAuthorizedChatRoomIds(sessionAttributes).add(chatRoomId);
    }

    private void validateChatRoomSendAuthorized(
            StompHeaderAccessor accessor,
            Map<String, Object> sessionAttributes
    ) {
        Long chatRoomId = extractChatRoomId(accessor.getDestination(), APP_CHATROOM_PREFIX);
        if (chatRoomId == null) {
            return;
        }

        if (!getAuthorizedChatRoomIds(sessionAttributes).contains(chatRoomId)) {
            throw new UnauthorizedChatRoomAccessException(
                    BusinessErrorMessage.UNAUTHORIZED_CHAT_ROOM_ACCESS.getMessage()
            );
        }
    }

    private Set<Long> getAuthorizedChatRoomIds(Map<String, Object> sessionAttributes) {
        return (Set<Long>) sessionAttributes.computeIfAbsent(
                AUTHORIZED_CHAT_ROOM_IDS_KEY,
                key -> ConcurrentHashMap.newKeySet()
        );
    }

    private Long extractChatRoomId(String destination, String prefix) {
        if (destination == null || !destination.startsWith(prefix)) {
            return null;
        }

        String value = destination.substring(prefix.length());
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException ex) {
            throw new UnauthorizedChatRoomAccessException(
                    BusinessErrorMessage.UNAUTHORIZED_CHAT_ROOM_ACCESS.getMessage()
            );
        }
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
