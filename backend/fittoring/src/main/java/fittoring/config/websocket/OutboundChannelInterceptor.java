package fittoring.config.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OutboundChannelInterceptor implements ChannelInterceptor, ExecutorChannelInterceptor {

    private static final String OUTBOUND_ENQUEUED_AT_NS_KEY = "wsOutboundEnqueuedAtNs";
    private static final String SERVER_INTERNAL_START_AT_NS_KEY = "wsServerInternalStartAtNs";

    private final WebSocketMetricsListener metricsListener;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        Object destination = message.getHeaders().get("simpDestination");
        if (destination == null) {
            return message;
        }

        MessageHeaderAccessor accessor = MessageHeaderAccessor.getMutableAccessor(message);
        accessor.setHeader(OUTBOUND_ENQUEUED_AT_NS_KEY, System.nanoTime());

        Long inboundEnqueuedAtNs = WebSocketMetricContext.getInboundEnqueuedAtNs();
        if (inboundEnqueuedAtNs != null) {
            accessor.setHeader(SERVER_INTERNAL_START_AT_NS_KEY, inboundEnqueuedAtNs);
        }

        return message;
    }

    @Override
    public Message<?> beforeHandle(Message<?> message, MessageChannel channel, MessageHandler handler) {
        Object destination = message.getHeaders().get("simpDestination");
        if (destination == null) {
            return message;
        }

        Long outboundEnqueuedAtNs = (Long) message.getHeaders().get(OUTBOUND_ENQUEUED_AT_NS_KEY);
        if (outboundEnqueuedAtNs != null) {
            metricsListener.recordOutboundQueueWait(System.nanoTime() - outboundEnqueuedAtNs);
        }

        Long serverInternalStartedAtNs = (Long) message.getHeaders().get(SERVER_INTERNAL_START_AT_NS_KEY);
        if (serverInternalStartedAtNs != null) {
            metricsListener.recordServerInternalEndToEnd(System.nanoTime() - serverInternalStartedAtNs);
        }

        return message;
    }

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
