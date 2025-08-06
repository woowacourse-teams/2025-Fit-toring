package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Certificate;
import fittoring.mentoring.business.model.Status;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends ListCrudRepository<Certificate, Long> {

    List<Certificate> findByVerificationStatus(Status statu);
}
