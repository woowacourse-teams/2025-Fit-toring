package fittoring.application.reservation.repository;

import fittoring.domain.model.SmsOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsOutboxRepository extends JpaRepository<SmsOutbox, Long> {
}
