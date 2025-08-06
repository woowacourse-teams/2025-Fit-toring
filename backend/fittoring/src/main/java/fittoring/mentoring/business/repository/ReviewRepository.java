package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Review;
import org.springframework.data.repository.ListCrudRepository;

public interface ReviewRepository extends ListCrudRepository<Review, Long> {

    boolean existsByReservationIdAndMenteeId(Long reservationId, Long mentee);
}
