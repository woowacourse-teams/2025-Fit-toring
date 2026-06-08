package fittoring.monitoring.sms;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class SmsOutboxPublisherMetrics {

    private final MeterRegistry meterRegistry;
    private final Timer publishTimer;
    private final DistributionSummary batchSizeSummary;

    public SmsOutboxPublisherMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.publishTimer = Timer.builder("sms_outbox_publish_duration_seconds")
                .description("Duration of sms_outbox publisher run")
                .publishPercentileHistogram(true)
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
        this.batchSizeSummary = DistributionSummary.builder("sms_outbox_publish_batch_size")
                .description("Claimed sms_outbox batch size per publisher run")
                .baseUnit("rows")
                .register(meterRegistry);
    }

    public Timer.Sample startPublish() {
        return Timer.start(meterRegistry);
    }

    public void stopPublish(Timer.Sample sample) {
        sample.stop(publishTimer);
    }

    public void incrementPublishRun(String result) {
        Counter.builder("sms_outbox_publish_runs_total")
                .description("Number of sms_outbox publisher runs by result")
                .tag("result", result)
                .register(meterRegistry)
                .increment();
    }

    public void recordBatchSize(int batchSize) {
        batchSizeSummary.record(batchSize);
    }

    public void incrementSendResult(String result, int count) {
        if (count <= 0) {
            return;
        }
        Counter.builder("sms_outbox_send_total")
                .description("Number of sms_outbox send results")
                .tag("result", result)
                .register(meterRegistry)
                .increment(count);
    }
}
