package fittoring.infrastructure.sms;

import fittoring.domain.model.Phone;
import fittoring.application.reservation.sms.SmsOutbox;
import fittoring.infrastructure.dto.BatchSendResult;
import fittoring.infrastructure.dto.SmsOutboxMessage;
import fittoring.infrastructure.exception.SmsException;
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

    @Value("${sms-outbox.publisher.enabled:true}")
    private boolean enabled;

    private volatile boolean shuttingDown;

    @PreDestroy
    public void onShutdown() {
        this.shuttingDown = true;
    }

    @Scheduled(fixedDelayString = "${sms-outbox.publisher.fixed-delay-ms:5000}")
    public void runScheduled() {
        if (!enabled || shuttingDown) {
            return;
        }
        publishPending();
    }

    public void publishPending() {
        if (shuttingDown) {
            return;
        }
        List<SmsOutbox> batch = claimService.claimPending();
        if (batch.isEmpty()) {
            return;
        }
        dispatchBatch(batch);
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
        for (SmsOutbox row : batch) {
            if (result.isFailed(row.getId())) {
                log.warn("배치 내 단건 SMS 발송 실패: outboxId={}, eventType={}", row.getId(), row.getEventType());
                resultApplier.applyFailure(row.getId(), "Recipient delivery failed in batch");
                continue;
            }
            resultApplier.applySuccess(row.getId());
        }
    }

    private void recordBatchFailure(List<SmsOutbox> batch, String reason) {
        for (SmsOutbox row : batch) {
            resultApplier.applyFailure(row.getId(), reason);
        }
    }
}
