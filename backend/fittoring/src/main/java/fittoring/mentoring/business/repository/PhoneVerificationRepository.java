package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.PhoneVerification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, Long> {

    Optional<PhoneVerification> findFirstByPhoneAndCodeOrderByExpireAtDesc(Phone phone, String code);

    Optional<PhoneVerification> findByPhone(Phone phone);
}
