package fittoring.config.websocket;

import fittoring.config.auth.LoginInfo;
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

        if (StompCommand.SEND.equals(accessor.getCommand())) {
            LoginInfo loginInfo = (LoginInfo) Objects.requireNonNull(accessor.getSessionAttributes())
                    .get(WebSocketAuthHandshakeInterceptor.LOGIN_INFO_KEY);
            accessor.setHeader(WebSocketAuthHandshakeInterceptor.LOGIN_INFO_KEY, loginInfo);

            accessor.setHeader(START_TIME_HEADER, System.nanoTime());
            metricsListener.incrementInboundMessage();

            return MessageBuilder.createMessage(
                    message.getPayload(),
                    accessor.getMessageHeaders()
            );
        }
        return message;
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
                metricsListener.incrementError();
                log.error("WebSocket message processing failed", ex);
            }
        }
    }
}
