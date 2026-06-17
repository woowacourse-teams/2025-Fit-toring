package fittoring.infrastructure.sms;

import fittoring.application.reservation.repository.SmsOutboxRepository;
import fittoring.application.reservation.sms.SmsOutbox;
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

    /**
     * 주어진 id들 중 claim 가능한(PENDING 또는 lease 만료된 PROCESSING) 행만 선점한다.
     * SQS 컨슈머가 받은 메시지의 outboxId만 처리할 때 사용하며, 폴러와의 경합·SQS 중복은
     * status + lease + SKIP LOCKED로 자동 제외된다.
     */
    @Transactional
    public List<SmsOutbox> claimByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime leaseCutoff = now.minusSeconds(leaseTimeoutSeconds);
        List<SmsOutbox> rows = smsOutboxRepository.findClaimableByIds(ids, leaseCutoff);
        for (SmsOutbox row : rows) {
            row.markProcessing(now);
        }
        return rows;
    }
}
