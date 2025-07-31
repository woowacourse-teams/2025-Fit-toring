package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.repository.PhoneVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PhoneVerificationService {

    private final PhoneVerificationRepository phoneVerificationRepository;

    @Transactional
    public void deletePendingPhoneVerification(Phone phone) {
        if (phoneVerificationRepository.existsByPhone(phone)) {
            phoneVerificationRepository.deleteByPhone(phone);
        }
    }
}
