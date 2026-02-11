package fittoring.config.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OutboundChannelInterceptor implements ChannelInterceptor {

    private final WebSocketMetricsListener metricsListener;

    /**
     * 메시지 전송 후 처리 - Outbound 메시지 카운트
     */
    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.MESSAGE.equals(accessor.getCommand())) {
            metricsListener.incrementOutboundMessage();
        }
    }
}
