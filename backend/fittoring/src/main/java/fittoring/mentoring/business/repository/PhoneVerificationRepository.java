package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.PhoneVerification;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneVerificationRepository extends ListCrudRepository<PhoneVerification, Long> {

    Optional<PhoneVerification> findFirstByPhoneAndCodeOrderByExpireAtDesc(Phone phone, String code);

    void deleteByPhone(Phone phone);
}
