package fittoring.monitoring.sms;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SmsOutboxDeliveryMetricsTest {

    private static final Pattern BUCKET_LE = Pattern.compile(
            "sms_outbox_delivery_latency_seconds_bucket\\{le=\"([0-9.]+)\"}"
    );

    @DisplayName("Outbox 생성부터 발송 성공까지 걸린 시간을 기록한다.")
    @Test
    void recordDeliveryLatency() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        SmsOutboxDeliveryMetrics metrics = new SmsOutboxDeliveryMetrics(registry);
        LocalDateTime createdAt = LocalDateTime.of(2026, 6, 10, 10, 0);

        metrics.record(createdAt, createdAt.plusSeconds(90));

        assertThat(registry.get("sms_outbox_delivery_latency_seconds")
                .timer()
                .totalTime(TimeUnit.SECONDS))
                .isEqualTo(90);
    }

    @DisplayName("분 단위 지연도 유한 버킷에 담겨 histogram_quantile로 p95/p99를 계산할 수 있다.")
    @Test
    void recordMinuteScaleLatencyInFiniteBuckets() {
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        SmsOutboxDeliveryMetrics metrics = new SmsOutboxDeliveryMetrics(registry);
        LocalDateTime createdAt = LocalDateTime.of(2026, 6, 10, 10, 0);

        // 예상 최대 backlog 지연(약 5분 50초)을 기록한다.
        metrics.record(createdAt, createdAt.plusSeconds(350));

        // 7분(420초) 합격 기준선을 넘는 유한 버킷이 있어야 p99가 +Inf로 뭉개지지 않는다.
        assertThat(maxFiniteBucketLe(registry.scrape()))
                .isGreaterThanOrEqualTo(420.0);
    }

    private double maxFiniteBucketLe(String scrape) {
        return scrape.lines()
                .map(BUCKET_LE::matcher)
                .filter(Matcher::find)
                .mapToDouble(matcher -> Double.parseDouble(matcher.group(1)))
                .max()
                .orElse(0.0);
    }
}
