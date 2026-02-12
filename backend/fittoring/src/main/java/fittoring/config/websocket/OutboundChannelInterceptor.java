package fittoring.config.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
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
        Object destination = message.getHeaders().get("simpDestination");
        if (destination != null) {
            metricsListener.incrementOutboundMessage();
        }
    }
}
