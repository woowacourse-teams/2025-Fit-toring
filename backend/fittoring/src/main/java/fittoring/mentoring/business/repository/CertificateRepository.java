package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Certificate;
import fittoring.mentoring.business.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends ListCrudRepository<Certificate, Long> {

    Page<Certificate> findAll(Pageable pageable);

    Page<Certificate> findByVerificationStatus(Status status, Pageable pageable);
}
