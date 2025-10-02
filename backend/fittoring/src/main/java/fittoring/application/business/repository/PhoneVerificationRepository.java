package fittoring.application.business.repository;

import fittoring.application.business.model.Phone;
import fittoring.application.business.model.PhoneVerification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, Long> {

    Optional<PhoneVerification> findFirstByPhoneAndCodeOrderByExpireAtDesc(Phone phone, String code);

    Optional<PhoneVerification> findByPhone(Phone phone);
}
