package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends ListCrudRepository<Reservation, Long> {

    List<Reservation> findAllByMentoringId(Long id);

    @Query("""
            SELECT r
            FROM Reservation r
            JOIN FETCH r.mentoring m
            JOIN FETCH r.mentee mt
            WHERE m.mentor.id = :mentorId
            """)
    List<Reservation> findAllByMentorId(Long mentorId);

    List<Reservation> findAllByMenteeId(Long menteeId);

    List<Reservation> findAllByMentoring(Mentoring mentoring);
}
