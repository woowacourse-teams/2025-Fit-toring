package fittoring.infrastructure;

import fittoring.domain.model.Phone;
import fittoring.domain.model.SmsOutbox;
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
        for (SmsOutbox row : batch) {
            dispatchAndApply(row);
        }
    }

    private void dispatchAndApply(SmsOutbox row) {
        try {
            smsRestClientService.sendSms(
                    new Phone(row.getToPhone()),
                    row.getMessage(),
                    row.getSubject()
            );
            resultApplier.applySuccess(row.getId());
        } catch (SmsException e) {
            log.warn("SMS 발송 실패: outboxId={}, eventType={}", row.getId(), row.getEventType(), e);
            resultApplier.applyFailure(row.getId(), e.getMessage());
        }
    }
}
