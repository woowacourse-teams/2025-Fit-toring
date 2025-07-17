package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Certificate;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends ListCrudRepository<Certificate, Long> {

}
