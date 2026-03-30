package fittoring.application.auth.service;

import fittoring.application.auth.presentation.dto.request.VerificationCodeRequest;
import fittoring.application.auth.repository.PhoneVerificationData;
import fittoring.application.auth.repository.PhoneVerificationRepository;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.InvalidPhoneVerificationException;
import fittoring.domain.model.Phone;
import fittoring.infrastructure.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PhoneVerificationService {

    private static final String INVALID_PHONE_VERIFICATION_MESSAGE = BusinessErrorMessage.PHONE_VERIFICATION_INVALID.getMessage();
    private static final int EXPIRE_TIME_SECONDS = 180;
    private static final int MAX_ATTEMPTS = 5;
    private final PhoneVerificationRepository phoneVerificationRepository;
    private final CodeGenerator verificationCodeGenerator;

    public String createPhoneVerification(Phone phone) {
        String generatedCode = verificationCodeGenerator.generate();
        phoneVerificationRepository.save(phone.getNumber(), generatedCode, EXPIRE_TIME_SECONDS);
        return generatedCode;
    }

    public void verifyCode(VerificationCodeRequest request) {
        String phoneNumber = request.phoneNumber();
        int attempts = phoneVerificationRepository.incrementAttempts(phoneNumber);
        if (attempts > MAX_ATTEMPTS) {
            phoneVerificationRepository.delete(phoneNumber);
            throw new InvalidPhoneVerificationException(INVALID_PHONE_VERIFICATION_MESSAGE);
        }
        PhoneVerificationData data = phoneVerificationRepository.findByPhone(phoneNumber)
                .orElseThrow(() -> new InvalidPhoneVerificationException(INVALID_PHONE_VERIFICATION_MESSAGE));
        if (data.verified() || !data.code().equals(request.code())) {
            throw new InvalidPhoneVerificationException(INVALID_PHONE_VERIFICATION_MESSAGE);
        }
        phoneVerificationRepository.markVerified(phoneNumber);
    }

    public void checkVerificationStatus(Phone phone) {
        PhoneVerificationData data = phoneVerificationRepository.findByPhone(phone.getNumber())
                .orElseThrow(() -> new InvalidPhoneVerificationException(INVALID_PHONE_VERIFICATION_MESSAGE));
        if (!data.verified()) {
            throw new InvalidPhoneVerificationException(INVALID_PHONE_VERIFICATION_MESSAGE);
        }
    }
}
