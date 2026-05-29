package fittoring.admin.presentation.dto;

import fittoring.application.reservation.sms.SmsOutbox;
import fittoring.application.reservation.sms.SmsOutboxEventType;
import fittoring.application.reservation.sms.SmsOutboxStatus;
import fittoring.domain.model.Phone;
import java.time.LocalDateTime;

public record AdminSmsOutboxDetailResponse(
        Long id,
        Long reservationId,
        SmsOutboxEventType eventType,
        String toPhone,
        String message,
        String subject,
        SmsOutboxStatus status,
        int attempts,
        String lastError,
        LocalDateTime processingStartedAt,
        LocalDateTime failedNotifiedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AdminSmsOutboxDetailResponse from(SmsOutbox row) {
        return new AdminSmsOutboxDetailResponse(
                row.getId(),
                row.getReservationId(),
                row.getEventType(),
                new Phone(row.getToPhone()).getMaskedNumber(),
                row.getMessage(),
                row.getSubject(),
                row.getStatus(),
                row.getAttempts(),
                row.getLastError(),
                row.getProcessingStartedAt(),
                row.getFailedNotifiedAt(),
                row.getCreatedAt(),
                row.getUpdatedAt()
        );
    }
}
