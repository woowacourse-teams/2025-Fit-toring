package fittoring.infrastructure.sms;

import fittoring.domain.model.Phone;
import fittoring.application.reservation.sms.SmsOutbox;
import fittoring.infrastructure.dto.BatchSendResult;
import fittoring.infrastructure.dto.SmsOutboxMessage;
import fittoring.infrastructure.exception.SmsException;
import fittoring.monitoring.sms.SmsOutboxPublisherMetrics;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PreDestroy;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsOutboxPublisher {

    private final SmsOutboxClaimer claimService;
    private final SmsRestClientService smsRestClientService;
    private final SmsOutboxResultApplier resultApplier;
    private final SmsOutboxPublisherMetrics metrics;

    @Value("${sms-outbox.publisher.enabled:true}")
    private boolean enabled;

    private volatile boolean shuttingDown;

    @PreDestroy
    public void onShutdown() {
        this.shuttingDown = true;
    }

    @Scheduled(fixedDelayString = "${sms-outbox.publisher.fixed-delay-ms:5000}")
    public void runScheduled() {
        if (!enabled) {
            metrics.incrementPublishRun("disabled");
            return;
        }
        if (shuttingDown) {
            metrics.incrementPublishRun("shutting_down");
            return;
        }
        publishPending();
    }

    public void publishPending() {
        Timer.Sample sample = metrics.startPublish();
        String result = "unknown";
        try {
            if (shuttingDown) {
                result = "shutting_down";
                return;
            }
            List<SmsOutbox> batch = claimService.claimPending();
            metrics.recordBatchSize(batch.size());
            if (batch.isEmpty()) {
                result = "empty";
                return;
            }
            dispatchBatch(batch);
            result = "dispatched";
        } catch (RuntimeException e) {
            result = "failed";
            throw e;
        } finally {
            metrics.incrementPublishRun(result);
            metrics.stopPublish(sample);
        }
    }

    private void dispatchBatch(List<SmsOutbox> batch) {
        List<SmsOutboxMessage> messages = getSmsOutboxMessages(batch);
        BatchSendResult result;
        try {
            result = smsRestClientService.sendBatch(messages);
        } catch (SmsException e) {
            log.warn("배치 SMS 발송 실패: batchSize={}", batch.size(), e);
            recordBatchFailure(batch, e.getMessage());
            return;
        } catch (Exception e) {
            log.error("배치 SMS 발송 중 예상치 못한 오류: batchSize={}", batch.size(), e);
            String reason = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
            recordBatchFailure(batch, reason);
            return;
        }
        applyPerRowResult(batch, result);
    }

    private List<SmsOutboxMessage> getSmsOutboxMessages(List<SmsOutbox> batch) {
        return batch.stream()
                .map(row -> new SmsOutboxMessage(
                        row.getId(),
                        new Phone(row.getToPhone()),
                        row.getMessage(),
                        row.getSubject()
                ))
                .toList();
    }

    private void applyPerRowResult(List<SmsOutbox> batch, BatchSendResult result) {
        int failedCount = 0;
        for (SmsOutbox row : batch) {
            if (result.isFailed(row.getId())) {
                log.warn("배치 내 단건 SMS 발송 실패: outboxId={}, eventType={}", row.getId(), row.getEventType());
                resultApplier.applyFailure(row.getId(), "배치 내 수신자 발송 실패");
                failedCount++;
                continue;
            }
            resultApplier.applySuccess(row.getId());
        }
        metrics.incrementSendResult("success", batch.size() - failedCount);
        metrics.incrementSendResult("failure", failedCount);
    }

    private void recordBatchFailure(List<SmsOutbox> batch, String reason) {
        metrics.incrementSendResult("failure", batch.size());
        for (SmsOutbox row : batch) {
            resultApplier.applyFailure(row.getId(), reason);
        }
    }
}
