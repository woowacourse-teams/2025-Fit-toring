package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Certificate;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Status;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends ListCrudRepository<Certificate, Long> {

    List<Certificate> findByVerificationStatus(Status statu);

    @Query("""
            SELECT c
            FROM Certificate c
            JOIN FETCH c.mentoring m
            WHERE m.id = :mentoringId AND c.verificationStatus = :status
            """)
    List<Certificate> findByMentoringIdAndVerificationStatus(
            @Param("mentoringId") Long mentoringId,
            @Param("status") Status status
    );

    List<Certificate> findAllByMentoringId(Long mentoringId);

    void deleteAllByMentoring(Mentoring mentoring);
}
