package fittoring.util;

import org.springframework.web.context.request.ServletRequestAttributes;

public class ResponseDurationCalculator {

    public static final String START_TIME_NS = "LOG_START_TIME_NS";

    private ResponseDurationCalculator() {
    }

    public static Long calculate(ServletRequestAttributes attrs) {
        if (attrs == null) {
            return null;
        }
        Object startedAt = attrs.getRequest().getAttribute(START_TIME_NS);
        if (startedAt instanceof Long startNs) {
            return (System.nanoTime() - startNs) / 1_000_000L;
        }
        return null;
    }
}
