package fittoring.mentoring.business.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.infra.SmsMessageFormatter;
import fittoring.mentoring.infra.SmsRestClientService;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PhoneVerificationFacadeServiceTest {

    @Mock
    private PhoneVerificationService phoneVerificationService;

    @Mock
    private SmsMessageFormatter smsMessageFormatter;

    @Mock
    private SmsRestClientService smsRestClientService;

    @InjectMocks
    private PhoneVerificationFacadeService phoneVerificationFacadeService;

    @DisplayName("전화번호 인증 코드를 생성하고 SMS로 전송한다.")
    @Test
    void sendPhoneVerificationCode_success() {
        // given
        String phoneNumber = "010-1234-5678";
        String code = "123456";
        String smsMessage = "핏토링 인증번호는 [" + code + "] 입니다.";

        BDDMockito.given(phoneVerificationService.createPhoneVerification(ArgumentMatchers.any(Phone.class)))
                .willReturn(code);
        BDDMockito.given(smsMessageFormatter.createSmsVerificationCodeMessage(code))
                .willReturn(smsMessage);

        // when
        phoneVerificationFacadeService.sendPhoneVerificationCode(phoneNumber);

        // then
        ArgumentCaptor<Phone> phoneCaptor = ArgumentCaptor.forClass(Phone.class);
        SoftAssertions.assertSoftly(softAssertions -> {
            Mockito.verify(phoneVerificationService).createPhoneVerification(phoneCaptor.capture());
            Mockito.verify(smsMessageFormatter).createSmsVerificationCodeMessage(code);
            Mockito.verify(smsRestClientService).sendSms(phoneCaptor.getValue(), smsMessage);
            assertThat(phoneCaptor.getValue().getNumber()).isEqualTo(phoneNumber);
        });
    }
}
