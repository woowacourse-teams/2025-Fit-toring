package fittoring.application.mentoring.repository;

import fittoring.application.mentoring.service.dto.ChatRoomMentoringInfoDto;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MentoringRepository extends ListCrudRepository<Mentoring, Long>, CustomMentoringRepository {

    @Query("""
                    SELECT m
                    FROM Review rv
                        JOIN rv.reservation res
                        JOIN res.mentoring m
                    WHERE rv.id = :reviewId
            """)
    Optional<Mentoring> findByReviewId(@Param("reviewId") Long reviewId);

    @Query(value = "SELECT * FROM mentoring WHERE id = :id AND is_deleted = true;", nativeQuery = true)
    Mentoring findDeletedById(@Param("id") Long id);

    @Query("""
            SELECT new fittoring.application.mentoring.service.dto.ChatRoomMentoringInfoDto(
                m.mentor.name,
                m.price,
                COALESCE(
                    (SELECT img.url
                       FROM Image img
                      WHERE img.relationId = m.id
                        AND img.imageType = 'MENTORING_PROFILE'
                        AND img.imageVariant = 'THUMBNAIL'),
                    (SELECT img.url
                       FROM Image img
                      WHERE img.relationId = m.id
                        AND img.imageType = 'MENTORING_PROFILE'
                        AND img.imageVariant = 'DEFAULT')
                )
            )
            FROM Mentoring m
            JOIN m.mentor
            WHERE m.id = :mentoringId
            """)
    Optional<ChatRoomMentoringInfoDto> findByIdForChatRoom(Long mentoringId);

    @Query("""
              SELECT m
              FROM Mentoring m
              JOIN FETCH m.mentor
              WHERE m.mentor.id = :mentorId
            """)
    Optional<Mentoring> findByMentorId(Long mentorId);

    boolean existsByMentor(Member member);

    @Query(value = "SELECT * FROM mentoring WHERE is_deleted = true", nativeQuery = true)
    List<Mentoring> findAllDeleted();
}
