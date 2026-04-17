package fittoring.monitoring.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class WebSocketMetricAspect {

    private final Timer chatMessageTimer;

    public WebSocketMetricAspect(MeterRegistry registry) {
        this.chatMessageTimer = Timer.builder("ws_handler_process_seconds")
                .description("Latency of @MessageMapping method execution")
                .publishPercentileHistogram(true)
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }

    @Around("@annotation(messageMapping)")
    public Object measureLatency(ProceedingJoinPoint joinPoint, MessageMapping messageMapping) throws Throwable {
        long startedAt = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            long elapsed = System.nanoTime() - startedAt;
            chatMessageTimer.record(elapsed, TimeUnit.NANOSECONDS);
        }
    }
}
