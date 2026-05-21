package fittoring.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sms_outbox")
@Entity
public class SmsOutbox {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 32)
    private SmsOutboxEventType eventType;

    @Column(name = "to_phone", nullable = false, length = 20)
    private String toPhone;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "subject", nullable = false, length = 100)
    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SmsOutboxStatus status;

    @Column(name = "attempts", nullable = false)
    private int attempts;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(name = "processing_started_at")
    private LocalDateTime processingStartedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static SmsOutbox pending(
            Long reservationId,
            SmsOutboxEventType eventType,
            Phone toPhone,
            String message,
            String subject
    ) {
        return new SmsOutbox(
                null,
                reservationId,
                eventType,
                toPhone.getNumber(),
                message,
                subject,
                SmsOutboxStatus.PENDING,
                0,
                null,
                null,
                null,
                null
        );
    }

    public void markProcessing(LocalDateTime now) {
        this.status = SmsOutboxStatus.PROCESSING;
        this.processingStartedAt = now;
    }

    public void markSent() {
        this.status = SmsOutboxStatus.SENT;
        this.processingStartedAt = null;
    }

    public void recordFailure(String error, int maxAttempts) {
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException("maxAttempts must be greater than 0");
        }
        this.attempts++;
        this.lastError = error;
        this.processingStartedAt = null;
        if (this.attempts >= maxAttempts) {
            this.status = SmsOutboxStatus.FAILED;
            return;
        }
        this.status = SmsOutboxStatus.PENDING;
    }
}
