package fittoring.infrastructure;

import fittoring.application.reservation.repository.SmsOutboxRepository;
import fittoring.domain.model.Phone;
import fittoring.domain.model.SmsOutbox;
import fittoring.domain.model.SmsOutboxStatus;
import fittoring.infrastructure.exception.SmsException;
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

    private final SmsOutboxRepository smsOutboxRepository;
    private final SmsRestClientService smsRestClientService;
    private final SmsOutboxResultApplier resultApplier;

    @Value("${sms-outbox.publisher.enabled:true}")
    private boolean enabled;

    @Scheduled(fixedDelayString = "${sms-outbox.publisher.fixed-delay-ms:5000}")
    public void runScheduled() {
        if (!enabled) {
            return;
        }
        publishPending();
    }

    public void publishPending() {
        List<SmsOutbox> batch = smsOutboxRepository.findTop10ByStatusOrderByCreatedAtAsc(SmsOutboxStatus.PENDING);
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
