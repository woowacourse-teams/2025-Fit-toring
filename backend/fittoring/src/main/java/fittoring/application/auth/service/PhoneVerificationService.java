package fittoring.application.auth.service;

import fittoring.application.auth.presentation.dto.request.VerificationCodeRequest;
import fittoring.application.auth.repository.PhoneVerificationRepository;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.InvalidPhoneVerificationException;
import fittoring.domain.model.Phone;
import fittoring.domain.model.PhoneVerification;
import fittoring.infrastructure.CodeGenerator;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PhoneVerificationService {

    private static final String INVALID_PHONE_VERIFICATION_MESSAGE = BusinessErrorMessage.PHONE_VERIFICATION_INVALID.getMessage();
    private static final int EXPIRE_TIME_MINUTE = 3;
    private final PhoneVerificationRepository phoneVerificationRepository;
    private final CodeGenerator verificationCodeGenerator;

    @Transactional
    public String createPhoneVerification(Phone phone) {
        PhoneVerification phoneVerification = phoneVerificationRepository.findByPhone(phone)
                .orElse(new PhoneVerification(phone, null, null));
        String generatedCode = verificationCodeGenerator.generate();
        phoneVerification.refresh(phone, generatedCode, calculateExpiredTime());
        phoneVerificationRepository.save(phoneVerification);
        return generatedCode;
    }

    private LocalDateTime calculateExpiredTime() {
        return LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                .plusMinutes(EXPIRE_TIME_MINUTE);
    }

    @Transactional
    public void verifyCode(VerificationCodeRequest request) {
        Phone phone = new Phone(request.phoneNumber());
        LocalDateTime requestTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        PhoneVerification phoneVerification = phoneVerificationRepository.findFirstByPhoneAndCodeOrderByExpireAtDesc(
                        phone,
                        request.code()
                )
                .orElseThrow(() -> new InvalidPhoneVerificationException(INVALID_PHONE_VERIFICATION_MESSAGE));
        if (phoneVerification.isExpired(requestTime) || phoneVerification.isVerified()) {
            throw new InvalidPhoneVerificationException(INVALID_PHONE_VERIFICATION_MESSAGE);
        }
        phoneVerification.verify();
    }

    @Transactional(readOnly = true)
    public void checkVerificationStatus(Phone phone) {
        PhoneVerification phoneVerification = phoneVerificationRepository.findByPhone(phone)
                .orElseThrow(() -> new InvalidPhoneVerificationException(INVALID_PHONE_VERIFICATION_MESSAGE));
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        if (phoneVerification.isExpired(now) || !phoneVerification.isVerified()) {
            throw new InvalidPhoneVerificationException(INVALID_PHONE_VERIFICATION_MESSAGE);
        }
    }
}
