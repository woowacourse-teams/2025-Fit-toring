package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.PhoneVerification;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneVerificationRepository extends ListCrudRepository<PhoneVerification, Long> {
}
