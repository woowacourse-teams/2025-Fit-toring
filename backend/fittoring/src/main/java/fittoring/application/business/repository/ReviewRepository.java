package fittoring.application.business.repository;

import fittoring.application.business.model.Reservation;
import fittoring.application.business.model.Review;
import java.util.List;
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
