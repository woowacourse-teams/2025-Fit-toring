package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;

public interface ReviewRepository extends ListCrudRepository<Review, Long> {

    Optional<Review> findByReservationId(Long reservationId);

    List<Review> findAlLByMenteeId(Long menteeId);

    boolean existsByReservationIdAndMenteeId(Long reservationId, Long mentee);
}
