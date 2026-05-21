package fittoring.infrastructure;

import fittoring.application.reservation.repository.SmsOutboxRepository;
import fittoring.domain.model.SmsOutbox;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SmsOutboxClaimer {

    private final SmsOutboxRepository smsOutboxRepository;
    private final int batchSize;
    private final int leaseTimeoutSeconds;

    public SmsOutboxClaimer(
            SmsOutboxRepository smsOutboxRepository,
            @Value("${sms-outbox.publisher.batch-size}") int batchSize,
            @Value("${sms-outbox.publisher.lease-timeout-seconds}") int leaseTimeoutSeconds
    ) {
        this.smsOutboxRepository = smsOutboxRepository;
        this.batchSize = batchSize;
        this.leaseTimeoutSeconds = leaseTimeoutSeconds;
    }

    @Transactional
    public List<SmsOutbox> claimPending() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime leaseCutoff = now.minusSeconds(leaseTimeoutSeconds);
        List<SmsOutbox> rows = smsOutboxRepository.findClaimable(leaseCutoff, batchSize);
        for (SmsOutbox row : rows) {
            row.markProcessing(now);
        }
        return rows;
    }
}
