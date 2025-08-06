package fittoring.mentoring.infra;

import fittoring.mentoring.business.service.dto.SmsReservationMessageDto;
import org.springframework.stereotype.Component;

@Component
public class SmsMessageFormatter {

    private static final String VERIFICATION_MESSAGE_PREFIX = "핏토링 인증번호는 [";
    private static final String VERIFICATION_MESSAGE_SUFFIX = "] 입니다.";

    public String createSmsReservationMessage(SmsReservationMessageDto response) {
        return String.format("""
                        멘토링 신청자: %s
                        상담 내용: %s
                        """,
                response.mentorName(),
                response.content()
        );
    }

    public String createSmsVerificationCodeMessage(String code) {
        return String.format("%s%s%s",
                VERIFICATION_MESSAGE_PREFIX,
                code,
                VERIFICATION_MESSAGE_SUFFIX
        );
    }
}
