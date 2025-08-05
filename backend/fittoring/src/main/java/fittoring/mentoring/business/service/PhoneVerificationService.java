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

    private final PhoneVerificationRepository phoneVerificationRepository;
    private final CodeGenerator verificationCodeGenerator;

    private static final int EXPIRE_TIME_MINUTE = 3;

    @Transactional
    public String createPhoneVerification(Phone phone) {
        PhoneVerification pv = phoneVerificationRepository.findByPhone(phone)
                .orElse(new PhoneVerification(phone, null, null));
        String generated = verificationCodeGenerator.generate();
        pv.refresh(phone, generated, calculateExpiredTime());
        phoneVerificationRepository.save(pv);
        return generated;
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
