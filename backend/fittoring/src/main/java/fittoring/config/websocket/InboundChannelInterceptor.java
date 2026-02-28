package fittoring.config.websocket;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ExpiredTokenException;
import fittoring.config.auth.LoginInfo;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Objects;
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

    private static final String START_TIME_HEADER = "metrics_start_time";

    private final WebSocketMetricsListener metricsListener;

    /**
     * STOMP 메시지 처리를 위한 인터셉터 - 인증 정보 전달 - 메트릭 수집 (레이턴시, 처리량, 에러율)
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();
        if (StompCommand.SEND.equals(command) || StompCommand.SUBSCRIBE.equals(command)) {
            validateSessionExpired(accessor);
        }

        if (StompCommand.SEND.equals(command)) {
            LoginInfo loginInfo = (LoginInfo) Objects.requireNonNull(accessor.getSessionAttributes())
                    .get(WebSocketAuthHandshakeInterceptor.LOGIN_INFO_KEY);
            accessor.setHeader(WebSocketAuthHandshakeInterceptor.LOGIN_INFO_KEY, loginInfo);

            accessor.setHeader(START_TIME_HEADER, System.nanoTime());
            metricsListener.incrementInboundMessage(resolvePayloadSize(message.getPayload()));

            return MessageBuilder.createMessage(
                    message.getPayload(),
                    accessor.getMessageHeaders()
            );
        }
        return message;
    }

    private void validateSessionExpired(StompHeaderAccessor accessor) {
        long expEpochMillis = getExpEpochMillis(accessor);
        validateTokenExpired(expEpochMillis);
    }

    private long getExpEpochMillis(StompHeaderAccessor accessor) {
        return (long) Objects.requireNonNull(accessor.getSessionAttributes())
                .get(WebSocketAuthHandshakeInterceptor.TOKEN_EXP_EPOCH_MILLIS_KEY);
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
     * 메시지 전송 완료 후 처리 - 레이턴시 측정 종료 - 에러 카운트
     */
    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, @Nullable Exception ex) {
        Long startTime = (Long) message.getHeaders().get(START_TIME_HEADER);

        if (startTime != null) {
            long duration = System.nanoTime() - startTime;
            metricsListener.recordMessageLatency(duration);

            if (ex != null) {
                metricsListener.incrementError(ex);
                log.error("WebSocket message processing failed", ex);
            }
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
