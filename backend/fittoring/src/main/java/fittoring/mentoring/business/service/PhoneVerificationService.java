package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.PhoneVerification;
import fittoring.mentoring.business.repository.PhoneVerificationRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PhoneVerificationService {

    private final PhoneVerificationRepository phoneVerificationRepository;
    private final CodeGenerator codeGenerator;

    @Transactional
    public String createPhoneVerification(Phone phone) {
        deleteExpiredVerification(phone);
        String code = codeGenerator.generate();
        PhoneVerification phoneVerification = new PhoneVerification(
                phone,
                code,
                LocalDateTime.now(ZoneId.of("Asia/Seoul")
                ));
        phoneVerificationRepository.save(phoneVerification);
        return code;
    }

    private void deleteExpiredVerification(Phone phone) {
        if (phoneVerificationRepository.existsByPhone(phone)) {
            phoneVerificationRepository.deleteByPhone(phone);
        }
    }
}
