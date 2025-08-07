package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Review;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;

public interface ReviewRepository extends ListCrudRepository<Review, Long> {

    List<Review> findAlLByMenteeId(Long menteeId);

    boolean existsByReservationIdAndMenteeId(Long reservationId, Long mentee);
}
