package fittoring.admin.presentation.dto;

import fittoring.application.reservation.sms.SmsOutbox;
import fittoring.application.reservation.sms.SmsOutboxEventType;
import fittoring.application.reservation.sms.SmsOutboxStatus;
import java.time.LocalDateTime;

public record AdminSmsOutboxResponse(
        Long id,
        Long reservationId,
        SmsOutboxEventType eventType,
        String toPhone,
        SmsOutboxStatus status,
        int attempts,
        String lastError,
        LocalDateTime failedNotifiedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AdminSmsOutboxResponse from(SmsOutbox row) {
        return new AdminSmsOutboxResponse(
                row.getId(),
                row.getReservationId(),
                row.getEventType(),
                maskPhone(row.getToPhone()),
                row.getStatus(),
                row.getAttempts(),
                row.getLastError(),
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
