package fittoring.monitoring.sms;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class SmsOutboxDeliveryMetrics {

    private final Timer deliveryLatency;

    public SmsOutboxDeliveryMetrics(MeterRegistry meterRegistry) {
        this.deliveryLatency = Timer.builder("sms_outbox_delivery_latency_seconds")
                .description("Latency from sms_outbox creation to successful delivery")
                .publishPercentileHistogram(true)
                .publishPercentiles(0.5, 0.95, 0.99)
                // 기본 percentile histogram 버킷은 30초에서 끊겨 분 단위 backlog 지연을 +Inf로 뭉갠다.
                // 적체 시 예상되는 분 단위 지연을 유한 버킷으로 담아 histogram_quantile이 p95/p99를 계산할 수 있게 한다.
                .minimumExpectedValue(Duration.ofSeconds(1))
                .maximumExpectedValue(Duration.ofMinutes(10))
                // 성공 기준선(p95 6분, p99 7분)을 정확히 읽을 수 있도록 명시적 SLO 버킷을 추가한다.
                .serviceLevelObjectives(Duration.ofMinutes(6), Duration.ofMinutes(7))
                .register(meterRegistry);
    }

    public void record(LocalDateTime createdAt, LocalDateTime sentAt) {
        deliveryLatency.record(Duration.between(createdAt, sentAt));
    }
}
