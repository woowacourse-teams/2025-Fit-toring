package fittoring.application.reservation.repository;

import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Reservation;
import fittoring.application.reservation.service.dto.ParticipatedReservationDto;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends ListCrudRepository<Reservation, Long> {

    List<Reservation> findAllByMentoringId(Long id);

    @Query("""
                select
                  r.id as reservationId,
                  m.id as mentoringId,
                  mentor.name as mentorName,
                  img.url as mentorProfileImage,
                  FUNCTION('date', r.createdAt) as reservedAt,
                  r.content as content,
                  r.status as status,
                  case when exists (
                    select 1 from Review rv where rv.isDeleted = false and rv.reservation = r
                  ) then true else false end as isReviewed
                from Reservation r
                  join r.mentoring m
                  join m.mentor mentor
                  left join Image img on
                       img.imageType = fittoring.domain.model.ImageType.MENTORING_PROFILE
                       and img.relationId = m.id
                where r.isDeleted = false
                    and r.mentee.id = :memberId
                order by r.createdAt desc, r.id desc
            """)
    List<ParticipatedReservationDto> findMemberReservationDtos(@Param("memberId") Long menteeId);

    @Query("""
            SELECT r
            FROM Reservation r
            JOIN FETCH r.mentoring m
            JOIN FETCH r.mentee mt
            WHERE m.mentor.id = :mentorId
            """)
    List<Reservation> findAllByMentorId(Long mentorId);

    List<Reservation> findAllByMentoring(Mentoring mentoring);
}
