package fittoring.monitoring.sms;

import fittoring.application.reservation.repository.SmsOutboxRepository;
import fittoring.application.reservation.sms.SmsOutboxStatus;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class SmsOutboxMetrics {

    private static final int MAX_TRACKED_ATTEMPTS = 3;

    private final SmsOutboxRepository smsOutboxRepository;

    public SmsOutboxMetrics(MeterRegistry registry, SmsOutboxRepository smsOutboxRepository) {
        this.smsOutboxRepository = smsOutboxRepository;

        for (SmsOutboxStatus status : SmsOutboxStatus.values()) {
            SmsOutboxStatus currentStatus = status;
            Gauge.builder("sms_outbox_rows", () -> smsOutboxRepository.countByStatus(currentStatus))
                    .description("Number of sms_outbox rows by status")
                    .tag("status", currentStatus.name())
                    .register(registry);
        }

        for (int attempts = 0; attempts <= MAX_TRACKED_ATTEMPTS; attempts++) {
            int currentAttempts = attempts;
            Gauge.builder("sms_outbox_attempts_rows", () -> smsOutboxRepository.countByAttempts(currentAttempts))
                    .description("Number of sms_outbox rows by attempts")
                    .tag("attempts", String.valueOf(currentAttempts))
                    .register(registry);
        }

        Gauge.builder("sms_outbox_pending_oldest_age_seconds", this::oldestPendingAgeSeconds)
                .description("Age of the oldest pending sms_outbox row")
                .register(registry);
    }

    private long oldestPendingAgeSeconds() {
        Optional<LocalDateTime> oldestCreatedAt = smsOutboxRepository.findOldestCreatedAtByStatus(
                SmsOutboxStatus.PENDING
        );
        return oldestCreatedAt
                .map(createdAt -> Duration.between(createdAt, LocalDateTime.now()).toSeconds())
                .orElse(0L);
    }
}
