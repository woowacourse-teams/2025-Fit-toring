package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Review;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends ListCrudRepository<Review, Long> {

    List<Review> findByReviewerId(Long reviewerId);

    List<Review> findByMentoringId(Long mentoringId);

    boolean existsByMentoringIdAndReviewerId(Long mentoringId, Long reviewerId);
}
