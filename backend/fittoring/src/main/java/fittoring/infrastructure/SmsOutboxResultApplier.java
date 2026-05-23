package fittoring.infrastructure;

import fittoring.application.reservation.repository.SmsOutboxRepository;
import fittoring.domain.model.SmsOutbox;
import fittoring.infrastructure.event.SmsOutboxFailedEvent;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SmsOutboxResultApplier {

    public static final int MAX_ATTEMPTS = 3;

    private final SmsOutboxRepository smsOutboxRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void applySuccess(Long id) {
        SmsOutbox row = smsOutboxRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        "SMS outbox row를 찾지 못했습니다. outboxId=" + id
                ));
        row.markSent();
    }

    @Transactional
    public void applyFailure(Long id, String error) {
        SmsOutbox row = smsOutboxRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        "SMS outbox row를 찾지 못했습니다. outboxId=" + id
                ));
        boolean newlyFailed = row.recordFailure(error, MAX_ATTEMPTS);
        if (newlyFailed) {
            eventPublisher.publishEvent(SmsOutboxFailedEvent.of(row));
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailedNotified(Long id, LocalDateTime now) {
        SmsOutbox row = smsOutboxRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        "SMS outbox row를 찾지 못했습니다. outboxId=" + id
                ));
        row.markFailedNotified(now);
    }
}
