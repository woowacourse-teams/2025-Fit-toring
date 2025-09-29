package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.MentoringStatistics;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

public interface MentoringStatisticsRepository extends ListCrudRepository<MentoringStatistics, Long> {

    @Modifying
    @Query("""
            UPDATE MentoringStatistics ms
            SET ms.reviewCount = ms.reviewCount + 1,
                ms.ratingSum = ms.ratingSum + :rating
            WHERE ms.id = :mentoringId
        """)
    void updateReviewStatisticsPlus(@Param("mentoringId") Long mentoringId, @Param("rating") int rating);

    @Modifying
    @Query("""
            UPDATE MentoringStatistics ms
            SET ms.reviewCount = ms.reviewCount - 1,
                ms.ratingSum = ms.ratingSum - :rating
            WHERE ms.id = :mentoringId
        """)
    void updateReviewStatisticsMinus(@Param("mentoringId") Long mentoringId, @Param("rating") int rating);

    @Modifying
    @Query("""
            UPDATE MentoringStatistics ms
            SET ms.reservationCount = ms.reservationCount + 1
            WHERE ms.id = :mentoringId
        """)
    void updateReservationCountPlus(@Param("mentoringId") Long mentoringId);

    @Modifying
    @Query("""
            UPDATE MentoringStatistics ms
            SET ms.reservationCount = ms.reservationCount - 1
            WHERE ms.id = :mentoringId
        """)
    void updateReservationCountMinus(Long mentoringId);
}
