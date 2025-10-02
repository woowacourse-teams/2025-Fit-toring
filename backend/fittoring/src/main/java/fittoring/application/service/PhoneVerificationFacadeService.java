package fittoring.application.service;

import fittoring.domain.model.Phone;
import fittoring.application.infra.SmsMessageFormatter;
import fittoring.application.infra.SmsRestClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PhoneVerificationFacadeService {

    private final PhoneVerificationService phoneVerificationService;
    private final SmsRestClientService smsRestClientService;
    private final SmsMessageFormatter smsMessageFormatter;

    public void sendPhoneVerificationCode(String phoneNumber) {
        Phone phone = new Phone(phoneNumber);
        String code = phoneVerificationService.createPhoneVerification(phone);
        String text = smsMessageFormatter.verificationCodeMessage(code);
        smsRestClientService.sendSms(phone, text);
    }
}
