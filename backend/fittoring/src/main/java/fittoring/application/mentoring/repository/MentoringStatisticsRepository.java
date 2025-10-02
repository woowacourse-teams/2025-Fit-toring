package fittoring.application.mentoring.repository;

import fittoring.domain.model.MentoringStatistics;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MentoringStatisticsRepository extends ListCrudRepository<MentoringStatistics, Long> {

    @Query("""
            SELECT ms
            FROM MentoringStatistics ms
            WHERE ms.id IN :mentoringIds
        """)
    List<MentoringStatistics> findByIds(List<Long> mentoringIds);

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE MentoringStatistics ms
            SET ms.reviewCount = ms.reviewCount + 1,
                ms.ratingSum = ms.ratingSum + :rating
            WHERE ms.id = :mentoringId
        """)
    void updateReviewStatisticsPlus(@Param("mentoringId") Long mentoringId, @Param("rating") int rating);

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE MentoringStatistics ms
            SET ms.reviewCount = ms.reviewCount - 1,
                ms.ratingSum = ms.ratingSum - :rating
            WHERE ms.id = :mentoringId
        """)
    void updateReviewStatisticsMinus(@Param("mentoringId") Long mentoringId, @Param("rating") int rating);

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE MentoringStatistics ms
            SET ms.reservationCount = ms.reservationCount + 1
            WHERE ms.id = :mentoringId
        """)
    void updateReservationCountPlus(@Param("mentoringId") Long mentoringId);

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE MentoringStatistics ms
            SET ms.reservationCount = ms.reservationCount - 1
            WHERE ms.id = :mentoringId
        """)
    void updateReservationCountMinus(Long mentoringId);
}
