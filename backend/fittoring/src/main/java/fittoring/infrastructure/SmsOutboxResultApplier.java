package fittoring.infrastructure;

import fittoring.application.reservation.repository.SmsOutboxRepository;
import fittoring.domain.model.SmsOutbox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SmsOutboxResultApplier {

    public static final int MAX_ATTEMPTS = 3;

    private final SmsOutboxRepository smsOutboxRepository;

    @Transactional
    public void applySuccess(Long id) {
        SmsOutbox row = smsOutboxRepository.findById(id).orElseThrow();
        row.markSent();
    }

    @Transactional
    public void applyFailure(Long id, String error) {
        SmsOutbox row = smsOutboxRepository.findById(id).orElseThrow();
        row.recordFailure(error, MAX_ATTEMPTS);
    }
}
