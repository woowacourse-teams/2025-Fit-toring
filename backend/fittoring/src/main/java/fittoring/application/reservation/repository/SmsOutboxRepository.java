package fittoring.application.reservation.repository;

import fittoring.application.reservation.sms.SmsOutbox;
import fittoring.application.reservation.sms.SmsOutboxStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SmsOutboxRepository extends JpaRepository<SmsOutbox, Long> {

    @Query(value = """
            SELECT *
            FROM sms_outbox
            WHERE status = 'PENDING'
               OR (status = 'PROCESSING' AND processing_started_at < :leaseCutoff)
            ORDER BY created_at ASC, id ASC
            LIMIT :batchSize
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<SmsOutbox> findClaimable(
            @Param("leaseCutoff") LocalDateTime leaseCutoff,
            @Param("batchSize") int batchSize
    );

    Page<SmsOutbox> findByStatus(SmsOutboxStatus status, Pageable pageable);

    long countByStatus(SmsOutboxStatus status);

    long countByAttempts(int attempts);

    @Query("""
            SELECT MIN(s.createdAt)
            FROM SmsOutbox s
            WHERE s.status = :status
            """)
    Optional<LocalDateTime> findOldestCreatedAtByStatus(@Param("status") SmsOutboxStatus status);
}
