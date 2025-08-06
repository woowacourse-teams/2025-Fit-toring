package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Reservation;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends ListCrudRepository<Reservation, Long> {

    List<Reservation> findAllByMentoringId(Long id);
}
