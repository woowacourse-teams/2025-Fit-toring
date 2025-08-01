package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.infra.SmsRestClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PhoneVerificationFacadeService {

    private static final String VERIFICATION_MESSAGE_PREFIX = "핏토링 인증번호는 [";
    private static final String VERIFICATION_MESSAGE_SUFFIX = "] 입니다.";

    private final PhoneVerificationService phoneVerificationService;
    private final SmsRestClientService smsRestClientService;

    public void sendPhoneVerificationCode(String phoneNumber) {
        Phone phone = new Phone(phoneNumber);
        String code = phoneVerificationService.createPhoneVerification(phone);
        smsRestClientService.sendSms(phone, VERIFICATION_MESSAGE_PREFIX + code + VERIFICATION_MESSAGE_SUFFIX);
    }
}
