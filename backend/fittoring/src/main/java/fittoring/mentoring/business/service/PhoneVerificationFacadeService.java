package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.infra.SmsMessageFormatter;
import fittoring.mentoring.infra.SmsRestClientService;
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
        String text = smsMessageFormatter.createSmsVerificationCodeMessage(code);
        smsRestClientService.sendSms(phone, text);
    }
}
