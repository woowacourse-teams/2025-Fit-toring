package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Review;
import fittoring.mentoring.business.service.dto.RatingStatsDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends ListCrudRepository<Review, Long> {

    Optional<Review> findByReservationId(Long reservationId);

    List<Review> findAllByMentee_Id(Long menteeId);

    @Query("""
            SELECT new fittoring.mentoring.business.service.dto.RatingStatsDto(
             res.mentoring.id, COALESCE(AVG(rv.rating), 0.0), COUNT(rv))
            FROM Review rv
            JOIN rv.reservation res
            WHERE res.mentoring.id = :mentoringId
            GROUP BY res.mentoring.id
            """)
    Optional<RatingStatsDto> findRatingStatsByMentoringId(@Param("mentoringId") Long mentoringId);

    @Query("""
            SELECT new fittoring.mentoring.business.service.dto.RatingStatsDto(m.id, AVG(r.rating), COUNT(r.id))
              FROM Review r
              JOIN r.reservation res
              JOIN res.mentoring m
             WHERE m.id IN :mentoringIds
             GROUP BY m.id
            """)
    List<RatingStatsDto> findReviewStatsByMentoringIds(@Param("mentoringIds") List<Long> mentoringIds);

    @Query("""
            SELECT r
              FROM Review r
              JOIN FETCH r.mentee
            WHERE r.reservation.mentoring.id = :mentoringId
            order by r.createdAt DESC
            """)
    List<Review> findAllByReservationMentoringIdOrderByCreatedAtDesc(@Param("mentoringId") Long mentoringId);

    boolean existsByReservationId(Long reservationId);

    boolean existsByReservationIdAndMentee_Id(Long reservationId, Long menteeId);

    void deleteByReservationId(Long reservationId);
}
