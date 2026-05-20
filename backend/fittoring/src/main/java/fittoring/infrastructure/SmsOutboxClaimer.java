package fittoring.infrastructure;

import fittoring.application.reservation.repository.SmsOutboxRepository;
import fittoring.domain.model.SmsOutbox;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SmsOutboxClaimer {

    private final SmsOutboxRepository smsOutboxRepository;

    @Value("${sms-outbox.publisher.batch-size}")
    private int batchSize;

    @Value("${sms-outbox.publisher.lease-timeout-seconds}")
    private int leaseTimeoutSeconds;

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
