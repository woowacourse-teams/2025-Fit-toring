package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Review;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends ListCrudRepository<Review, Long> {

    boolean existsByMentoringIdAndReviewerId(Long mentoringId, Long reviewerId);
}
