package fittoring.infrastructure.sms;

import fittoring.application.reservation.sms.SmsOutbox;
import fittoring.monitoring.sms.SmsDispatchSqsMetrics;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.micrometer.core.instrument.Timer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * SMS 빠른 길 컨슈머. 커밋 후 발행된 outboxId를 배치로 받아 claim 후 발송한다.
 * 폴러와 동일한 claim(lease + SKIP LOCKED)을 거치므로 SQS 중복·폴러 경합은 자동 제외된다.
 *
 * <p>플래그가 켜진 경우에만 빈으로 등록한다. @SqsListener는 빈 생성 즉시 큐에 연결을 시도하므로,
 * 플래그 off(기본)에서는 큐가 없어도 안전하게 비활성화된다.
 */
@Component
@Profile({"!local & !test"})
@ConditionalOnProperty(prefix = "sms.dispatch.sqs", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class SmsDispatchSqsListener {

    private final SmsOutboxClaimer claimer;
    private final SmsOutboxPublisher publisher;
    private final SmsDispatchSqsMetrics metrics;

    @SqsListener(value = "${aws.sqs.sms-dispatch-queue}", factory = "smsDispatchSqsListenerContainerFactory")
    public void handle(List<Long> outboxIds) {
        Timer.Sample sample = metrics.startListener();
        String result = "unknown";
        try {
            metrics.incrementListenerMessages("received", outboxIds.size());
            metrics.recordBatchSize("received", outboxIds.size());
            List<SmsOutbox> claimed = claimer.claimByIds(outboxIds);
            metrics.incrementListenerMessages("claimed", claimed.size());
            metrics.recordBatchSize("claimed", claimed.size());
            if (claimed.isEmpty()) {
                result = "empty_claim";
                return;
            }
            publisher.dispatch(claimed);
            metrics.incrementListenerMessages("dispatched", claimed.size());
            result = "dispatched";
        } catch (RuntimeException e) {
            result = "failed";
            throw e;
        } finally {
            metrics.incrementListenerBatch(result);
            metrics.stopListener(sample);
        }
    }
}
