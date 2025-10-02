package fittoring.application.auth.service;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.InvalidPhoneVerificationException;
import fittoring.application.service.CodeGenerator;
import fittoring.domain.model.Phone;
import fittoring.domain.model.PhoneVerification;
import fittoring.application.repository.PhoneVerificationRepository;
import fittoring.application.auth.presentation.dto.request.VerificationCodeRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PhoneVerificationService {

    private final PhoneVerificationRepository phoneVerificationRepository;
    private final CodeGenerator verificationCodeGenerator;

    private static final int EXPIRE_TIME_MINUTE = 3;

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

    public void verifyCode(VerificationCodeRequest request) {
        Phone phone = new Phone(request.phone());
        LocalDateTime requestTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        PhoneVerification phoneVerification = phoneVerificationRepository.findFirstByPhoneAndCodeOrderByExpireAtDesc(
                        phone,
                        request.code()
                )
                .orElseThrow(() -> new InvalidPhoneVerificationException(
                        BusinessErrorMessage.PHONE_VERIFICATION_INVALID.getMessage()
                ));
        if (phoneVerification.isExpired(requestTime)) {
            throw new InvalidPhoneVerificationException(BusinessErrorMessage.PHONE_VERIFICATION_INVALID.getMessage());
        }
    }
}
