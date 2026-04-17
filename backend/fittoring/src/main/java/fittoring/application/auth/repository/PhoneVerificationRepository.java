package fittoring.application.auth.repository;

import java.util.Optional;

public interface PhoneVerificationRepository {

    void save(String phoneNumber, String code, long ttlSeconds);

    Optional<PhoneVerificationData> findByPhone(String phoneNumber);

    void markVerified(String phoneNumber);

    int incrementAttempts(String phoneNumber);

    void delete(String phoneNumber);
}
