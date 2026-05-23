package fittoring.admin.presentation.dto;

import fittoring.domain.model.SmsOutbox;
import fittoring.domain.model.SmsOutboxEventType;
import fittoring.domain.model.SmsOutboxStatus;
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
                maskPhone(row.getToPhone()),
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

    private static String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return phone;
        }
        return phone.substring(0, phone.length() - 4) + "****";
    }
}
