package fittoring.infrastructure.event;

import fittoring.domain.model.SmsOutbox;
import fittoring.domain.model.SmsOutboxEventType;

public record SmsOutboxFailedEvent(
        Long outboxId,
        Long reservationId,
        SmsOutboxEventType eventType,
        String toPhone,
        int attempts,
        String lastError
) {
    public static SmsOutboxFailedEvent of(SmsOutbox row) {
        return new SmsOutboxFailedEvent(
                row.getId(),
                row.getReservationId(),
                row.getEventType(),
                row.getToPhone(),
                row.getAttempts(),
                row.getLastError()
        );
    }
}
