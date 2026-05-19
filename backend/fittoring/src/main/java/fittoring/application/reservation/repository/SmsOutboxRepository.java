package fittoring.application.reservation.repository;

import fittoring.domain.model.SmsOutbox;
import fittoring.domain.model.SmsOutboxStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsOutboxRepository extends JpaRepository<SmsOutbox, Long> {

    List<SmsOutbox> findTop10ByStatusOrderByCreatedAtAsc(SmsOutboxStatus status);
}
