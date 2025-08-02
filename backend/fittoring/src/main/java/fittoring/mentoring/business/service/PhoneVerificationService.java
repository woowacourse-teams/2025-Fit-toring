package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.InvalidPhoneVerificationException;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.PhoneVerification;
import fittoring.mentoring.business.repository.PhoneVerificationRepository;
import fittoring.mentoring.presentation.dto.VerificationCodeRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PhoneVerificationService {

    private static final int EXPIRE_TIME_MINUTE = 3;

    private final PhoneVerificationRepository phoneVerificationRepository;
    private final CodeGenerator verificationCodeGenerator;

    @Transactional
    public String createPhoneVerification(Phone phone) {
        deleteExpiredVerification(phone);
        String code = verificationCodeGenerator.generate();
        LocalDateTime expiredDateTime = calculateExpiredTime();
        PhoneVerification phoneVerification = new PhoneVerification(
                phone,
                code,
                expiredDateTime
        );
        phoneVerificationRepository.save(phoneVerification);
        return code;
    }

    private void deleteExpiredVerification(Phone phone) {
        if (phoneVerificationRepository.existsByPhone(phone)) {
            phoneVerificationRepository.deleteByPhone(phone);
        }
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
        if (phoneVerification.expiredStatus(requestTime)) {
            throw new InvalidPhoneVerificationException(BusinessErrorMessage.PHONE_VERIFICATION_INVALID.getMessage());
        }
    }
}
