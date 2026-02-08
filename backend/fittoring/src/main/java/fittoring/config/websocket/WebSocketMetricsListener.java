package fittoring.config.websocket;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
public class WebSocketMetricsListener {

    private final Set<String> activeSessions = ConcurrentHashMap.newKeySet();
    private final Counter stompFramesConnect;
    private final Counter stompFramesConnected;
    private final Counter stompFramesDisconnect;
    private final Counter stompSessionsEvents;
    private final Counter stompSubscriptionsEvents;
    private final Counter websocketBrokerAvailability;

    public WebSocketMetricsListener(MeterRegistry meterRegistry) {
        this.stompFramesConnect = meterRegistry.counter("stomp_frames_connect_total");
        this.stompFramesConnected = meterRegistry.counter("stomp_frames_connected_total");
        this.stompFramesDisconnect = meterRegistry.counter("stomp_frames_disconnect_total");
        this.stompSessionsEvents = meterRegistry.counter("stomp_sessions_events_total");
        this.stompSubscriptionsEvents = meterRegistry.counter("stomp_subscriptions_events_total");
        this.websocketBrokerAvailability = meterRegistry.counter("websocket_broker_availability_events_total");
        Gauge.builder("websocket_sessions_active", activeSessions, Set::size)
                .register(meterRegistry);
    }

    @EventListener
    public void onSessionConnect(SessionConnectEvent event) {
        stompFramesConnect.increment();
        stompSessionsEvents.increment();
    }

    @EventListener
    public void onSessionConnected(SessionConnectedEvent event) {
        stompFramesConnected.increment();
        stompSessionsEvents.increment();
        String sessionId = headerSessionId(event);
        if (sessionId != null) {
            activeSessions.add(sessionId);
        }
    }

    @EventListener
    public void onSessionDisconnect(SessionDisconnectEvent event) {
        stompFramesDisconnect.increment();
        stompSessionsEvents.increment();
        String sessionId = headerSessionId(event);
        if (sessionId != null) {
            activeSessions.remove(sessionId);
        }
    }

    @EventListener
    public void onSessionSubscribe(SessionSubscribeEvent event) {
        stompSubscriptionsEvents.increment();
    }

    @EventListener
    public void onSessionUnsubscribe(SessionUnsubscribeEvent event) {
        stompSubscriptionsEvents.increment();
    }

    @EventListener
    public void onBrokerAvailability(BrokerAvailabilityEvent event) {
        websocketBrokerAvailability.increment();
    }

    private String headerSessionId(Object event) {
        if (event instanceof SessionConnectEvent connectEvent) {
            return StompHeaderAccessor.wrap(connectEvent.getMessage()).getSessionId();
        }
        if (event instanceof SessionConnectedEvent connectedEvent) {
            return StompHeaderAccessor.wrap(connectedEvent.getMessage()).getSessionId();
        }
        if (event instanceof SessionDisconnectEvent disconnectEvent) {
            return StompHeaderAccessor.wrap(disconnectEvent.getMessage()).getSessionId();
        }
        return null;
    }
}
