package fittoring.monitoring.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class WebSocketMetricAspect {

    private final Timer chatMessageTimer;

    public WebSocketMetricAspect(MeterRegistry registry) {
        this.chatMessageTimer = Timer.builder("ws_message_process_seconds")
                .description("Latency of chat message processing")
                .publishPercentileHistogram(true)
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }

    @Around("@annotation(org.springframework.messaging.handler.annotation.MessageMapping)")
    public Object measureLatency(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start();
        try {
            return joinPoint.proceed();
        } finally {
            sample.stop(chatMessageTimer);
        }
    }
}
