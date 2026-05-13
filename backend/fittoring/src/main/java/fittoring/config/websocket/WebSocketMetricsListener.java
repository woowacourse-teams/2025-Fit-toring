package fittoring.config.websocket;

import fittoring.application.exception.ExpiredTokenException;
import fittoring.application.exception.InvalidTokenException;
import fittoring.application.exception.UnauthorizedException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

@Slf4j
@Component
public class WebSocketMetricsListener {

    /**
     * 현재 활성 세션
     */
    private final Set<String> activeSessions = ConcurrentHashMap.newKeySet();
    private final Map<String, Long> sessionConnectTimes = new ConcurrentHashMap<>();
    private final MeterRegistry meterRegistry;

    /**
     * STOMP Frame Counters
     */
    private final Counter stompConnect;
    private final Counter stompConnected;
    private final Counter stompDisconnect;
    private final Counter stompSubscribe;
    private final Counter stompUnsubscribe;

    /**
     * Message Counters
     */
    private final Counter wsMessageIn;
    private final Counter wsMessageOut;

    /**
     * Error Counter
     */
    private final Counter wsMessageError;
    private final Counter handshakeFailureAuth;
    private final Counter handshakeFailureOther;

    private final Timer wsSessionDuration;
    private final Timer wsInboundQueueWait;
    private final Timer wsOutboundQueueWait;
    private final Timer wsServerInternalEndToEnd;
    private final DistributionSummary wsInboundMessageSizeBytes;

    public WebSocketMetricsListener(
            MeterRegistry meterRegistry,
            WebSocketMessageBrokerStats webSocketMessageBrokerStats,
            @Qualifier("clientInboundChannelExecutor") Executor inbound,
            @Qualifier("clientOutboundChannelExecutor") Executor outbound
    ) {
        this.meterRegistry = meterRegistry;

        // ---------- Counters ----------
        this.stompConnect = meterRegistry.counter("ws_stomp_connect_total");
        this.stompConnected = meterRegistry.counter("ws_stomp_connected_total");
        this.stompDisconnect = meterRegistry.counter("ws_stomp_disconnect_total");
        this.stompSubscribe = meterRegistry.counter("ws_stomp_subscribe_total");
        this.stompUnsubscribe = meterRegistry.counter("ws_stomp_unsubscribe_total");
        this.handshakeFailureAuth = meterRegistry.counter("ws_handshake_failure_total", "failure_type", "auth");
        this.handshakeFailureOther = meterRegistry.counter("ws_handshake_failure_total", "failure_type", "other");

        // ---------- Message Counters ----------
        this.wsMessageIn = meterRegistry.counter("ws_message_in_total");
        this.wsMessageOut = meterRegistry.counter("ws_message_out_total");
        this.wsInboundMessageSizeBytes = DistributionSummary.builder("ws_message_size_bytes")
                .description("Inbound STOMP message payload size")
                .baseUnit("bytes")
                .tag("direction", "inbound")
                .publishPercentileHistogram(true)
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);

        // ---------- Error Counter ----------
        this.wsMessageError = meterRegistry.counter("ws_message_error_total");

        this.wsSessionDuration = Timer.builder("ws_session_duration_seconds")
                .description("WebSocket session duration")
                .publishPercentileHistogram(true)
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
        this.wsInboundQueueWait = Timer.builder("ws_inbound_queue_wait_seconds")
                .description("Time spent waiting in the clientInboundChannel queue before handler execution")
                .publishPercentileHistogram(true)
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
        this.wsOutboundQueueWait = Timer.builder("ws_outbound_queue_wait_seconds")
                .description("Time spent waiting in the clientOutboundChannel queue before delivery processing")
                .publishPercentileHistogram(true)
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
        this.wsServerInternalEndToEnd = Timer.builder("ws_server_internal_end_to_end_seconds")
                .description("Server-internal end-to-end latency from inbound enqueue to outbound delivery handling")
                .publishPercentileHistogram(true)
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);

        ThreadPoolTaskExecutor inboundExec = (ThreadPoolTaskExecutor) inbound;
        ThreadPoolTaskExecutor outboundExec = (ThreadPoolTaskExecutor) outbound;

        // ---------- Active Session Gauge ----------
        Gauge.builder("ws_sessions_active_count", activeSessions, Set::size)
                .description("Current active STOMP sessions")
                .register(meterRegistry);

        Gauge.builder("ws_transport_sessions_current_count",
                        webSocketMessageBrokerStats,
                        this::getCurrentTransportSessionCount)
                .description("Current WebSocket transport sessions managed by Spring")
                .register(meterRegistry);

        Gauge.builder("ws_session_count_gap",
                        webSocketMessageBrokerStats,
                        this::calculateSessionCountGap)
                .description("Gap between active STOMP sessions and current transport sessions")
                .register(meterRegistry);

        // ---------- Inbound Executor Gauges ----------
        Gauge.builder("ws_inbound_pool_size",
                        inboundExec,
                        ThreadPoolTaskExecutor::getPoolSize)
                .register(meterRegistry);

        Gauge.builder("ws_inbound_active_threads",
                        inboundExec,
                        ThreadPoolTaskExecutor::getActiveCount)
                .register(meterRegistry);

        Gauge.builder("ws_inbound_queue_size",
                        inboundExec,
                        ex -> ex.getThreadPoolExecutor().getQueue().size())
                .register(meterRegistry);

        // ---------- Outbound Executor Gauges ----------
        Gauge.builder("ws_outbound_pool_size",
                        outboundExec,
                        ThreadPoolTaskExecutor::getPoolSize)
                .register(meterRegistry);

        Gauge.builder("ws_outbound_active_threads",
                        outboundExec,
                        ThreadPoolTaskExecutor::getActiveCount)
                .register(meterRegistry);

        Gauge.builder("ws_outbound_queue_size",
                        outboundExec,
                        ex -> ex.getThreadPoolExecutor().getQueue().size())
                .register(meterRegistry);
    }

    // ---------- Event Listeners ----------

    @EventListener
    public void onSessionConnect(SessionConnectEvent event) {
        stompConnect.increment();
    }

    @EventListener
    public void onSessionConnected(SessionConnectedEvent event) {
        stompConnected.increment();

        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();
        if (sessionId != null) {
            activeSessions.add(sessionId);
            sessionConnectTimes.put(sessionId, System.currentTimeMillis());
        }
    }

    @EventListener
    public void onSessionDisconnect(SessionDisconnectEvent event) {
        stompDisconnect.increment();
        incrementDisconnectByCloseStatus(event.getCloseStatus());

        String sessionId = event.getSessionId();
        if (sessionId != null) {
            activeSessions.remove(sessionId);
            Long connectedAt = sessionConnectTimes.remove(sessionId);
            if (connectedAt != null) {
                wsSessionDuration.record(System.currentTimeMillis() - connectedAt, TimeUnit.MILLISECONDS);
            }
        }
    }

    @EventListener
    public void onSessionSubscribe(SessionSubscribeEvent event) {
        stompSubscribe.increment();
    }

    @EventListener
    public void onSessionUnsubscribe(SessionUnsubscribeEvent event) {
        stompUnsubscribe.increment();
    }

    /**
     * Inbound message increment
     */
    public void incrementInboundMessage(int payloadSizeBytes) {
        wsMessageIn.increment();
        wsInboundMessageSizeBytes.record(Math.max(payloadSizeBytes, 0));
    }

    /**
     * Outbound message increment
     */
    public void incrementOutboundMessage() {
        wsMessageOut.increment();
    }

    public void recordInboundQueueWait(long waitNanos) {
        if (waitNanos < 0) {
            return;
        }
        wsInboundQueueWait.record(waitNanos, TimeUnit.NANOSECONDS);
    }

    public void recordOutboundQueueWait(long waitNanos) {
        if (waitNanos < 0) {
            return;
        }
        wsOutboundQueueWait.record(waitNanos, TimeUnit.NANOSECONDS);
    }

    public void recordServerInternalEndToEnd(long elapsedNanos) {
        if (elapsedNanos < 0) {
            return;
        }
        wsServerInternalEndToEnd.record(elapsedNanos, TimeUnit.NANOSECONDS);
    }

    /**
     * Error increment
     */
    public void incrementError(Exception ex) {
        wsMessageError.increment();
        meterRegistry.counter("ws_message_error_type_total", "error_type", classifyErrorType(ex)).increment();
    }

    public void incrementHandshakeFailure(String failureType) {
        if ("auth".equals(failureType)) {
            handshakeFailureAuth.increment();
            return;
        }
        handshakeFailureOther.increment();
    }

    private String classifyErrorType(Exception ex) {
        Throwable cursor = ex;
        int depth = 0;
        while (cursor != null && depth < 5) {
            if (cursor instanceof UnauthorizedException
                    || cursor instanceof InvalidTokenException
                    || cursor instanceof ExpiredTokenException) {
                return "auth";
            }
            if (cursor instanceof IllegalArgumentException) {
                return "validation";
            }
            if (cursor instanceof MessageDeliveryException) {
                return "broker";
            }
            cursor = cursor.getCause();
            depth++;
        }
        return "internal";
    }

    private void incrementDisconnectByCloseStatus(CloseStatus closeStatus) {
        String closeCode = "unknown";
        if (closeStatus != null) {
            closeCode = String.valueOf(closeStatus.getCode());
        }

        meterRegistry.counter("ws_disconnect_close_status_total", "close_code", closeCode).increment();
    }

    private double getCurrentTransportSessionCount(WebSocketMessageBrokerStats webSocketMessageBrokerStats) {
        SubProtocolWebSocketHandler.Stats stats = webSocketMessageBrokerStats.getWebSocketSessionStats();
        if (stats == null) {
            return 0;
        }
        return stats.getWebSocketSessions()
                + stats.getHttpStreamingSessions()
                + stats.getHttpPollingSessions();
    }

    private double calculateSessionCountGap(WebSocketMessageBrokerStats webSocketMessageBrokerStats) {
        return activeSessions.size() - getCurrentTransportSessionCount(webSocketMessageBrokerStats);
    }
}
