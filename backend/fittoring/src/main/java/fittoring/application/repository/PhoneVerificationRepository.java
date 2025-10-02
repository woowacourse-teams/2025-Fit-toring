package fittoring.application.repository;

import fittoring.domain.model.Phone;
import fittoring.domain.model.PhoneVerification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, Long> {

    Optional<PhoneVerification> findFirstByPhoneAndCodeOrderByExpireAtDesc(Phone phone, String code);

    Optional<PhoneVerification> findByPhone(Phone phone);
}
