package fittoring.config.websocket;

public final class WebSocketMetricContext {

    private static final ThreadLocal<Long> INBOUND_ENQUEUED_AT_NS = new ThreadLocal<>();

    private WebSocketMetricContext() {
    }

    public static void setInboundEnqueuedAtNs(long value) {
        INBOUND_ENQUEUED_AT_NS.set(value);
    }

    public static Long getInboundEnqueuedAtNs() {
        return INBOUND_ENQUEUED_AT_NS.get();
    }

    public static void clear() {
        INBOUND_ENQUEUED_AT_NS.remove();
    }
}
