package fittoring.application.reservation.repository;

import fittoring.domain.model.SmsOutbox;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SmsOutboxRepository extends JpaRepository<SmsOutbox, Long> {

    @Query(value = """
            SELECT *
            FROM sms_outbox
            WHERE status = 'PENDING'
               OR (status = 'PROCESSING' AND processing_started_at < :leaseCutoff)
            ORDER BY created_at ASC
            LIMIT :batchSize
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<SmsOutbox> findClaimable(
            @Param("leaseCutoff") LocalDateTime leaseCutoff,
            @Param("batchSize") int batchSize
    );
}
