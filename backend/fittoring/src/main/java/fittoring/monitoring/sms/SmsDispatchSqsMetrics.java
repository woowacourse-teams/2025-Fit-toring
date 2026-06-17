package fittoring.monitoring.sms;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class SmsDispatchSqsMetrics {

    private final MeterRegistry meterRegistry;
    private final Timer listenerTimer;
    private final AtomicInteger activeListeners = new AtomicInteger();

    public SmsDispatchSqsMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.listenerTimer = Timer.builder("sms_dispatch_sqs_listener_duration_seconds")
                .description("Duration of sms dispatch SQS listener batch handling")
                .publishPercentileHistogram(true)
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
        Gauge.builder("sms_dispatch_sqs_listener_active", activeListeners, AtomicInteger::get)
                .description("Number of active sms dispatch SQS listener invocations")
                .register(meterRegistry);
    }

    public void incrementPublish(String result) {
        Counter.builder("sms_dispatch_sqs_publish_total")
                .description("Number of sms dispatch SQS publish attempts by result")
                .tag("result", result)
                .register(meterRegistry)
                .increment();
    }

    public Timer.Sample startListener() {
        activeListeners.incrementAndGet();
        return Timer.start(meterRegistry);
    }

    public void stopListener(Timer.Sample sample) {
        try {
            sample.stop(listenerTimer);
        } finally {
            activeListeners.decrementAndGet();
        }
    }

    public void incrementListenerBatch(String result) {
        Counter.builder("sms_dispatch_sqs_listener_batches_total")
                .description("Number of sms dispatch SQS listener batches by result")
                .tag("result", result)
                .register(meterRegistry)
                .increment();
    }

    public void incrementListenerMessages(String stage, int count) {
        if (count <= 0) {
            return;
        }
        Counter.builder("sms_dispatch_sqs_listener_messages_total")
                .description("Number of sms dispatch SQS listener messages by stage")
                .tag("stage", stage)
                .register(meterRegistry)
                .increment(count);
    }

    public void recordBatchSize(String stage, int count) {
        DistributionSummary.builder("sms_dispatch_sqs_listener_batch_size")
                .description("SMS dispatch SQS listener batch size by stage")
                .baseUnit("messages")
                .tag("stage", stage)
                .register(meterRegistry)
                .record(count);
    }
}
