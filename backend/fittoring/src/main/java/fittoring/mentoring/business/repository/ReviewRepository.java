package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.model.Review;
import fittoring.mentoring.business.service.dto.RatingStatsDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends ListCrudRepository<Review, Long> {

    List<Review> findAllByMentee_Id(Long menteeId);

    @Query("""
            SELECT r
              FROM Review r
              JOIN FETCH r.reservation rv
              JOIN FETCH rv.mentoring mt
              JOIN FETCH r.mentee m
            WHERE mt.id = :mentoringId
            ORDER BY r.createdAt DESC
            """)
    List<Review> findAllByReservationMentoringIdOrderByCreatedAtDesc(@Param("mentoringId") Long mentoringId);

    boolean existsByReservationIdAndMentee_Id(Long reservationId, Long menteeId);

    void deleteByReservation(Reservation reservation);
}
