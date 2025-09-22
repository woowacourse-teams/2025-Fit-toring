package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends ListCrudRepository<Reservation, Long> {

    List<Reservation> findByMentoringId(Long mentoringId);

    List<Reservation> findAllByMentoringId(Long id);

    @Query("""
    select distinct r
    from Reservation r
    join fetch r.mentoring m
    join fetch m.mentor mentor
    join fetch r.mentee
    where r.isDeleted = false
    and r.mentee.id = :menteeId
    order by r.createdAt desc , r.id asc
""")
    List<Reservation> findAllByMenteeId(Long menteeId);

    List<Reservation> findAllByMentoring(Mentoring mentoring);
}
