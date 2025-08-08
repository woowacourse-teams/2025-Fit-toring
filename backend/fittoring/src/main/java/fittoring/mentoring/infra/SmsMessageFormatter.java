package fittoring.mentoring.infra;

import org.springframework.stereotype.Component;

@Component
public class SmsMessageFormatter {

    private static final String VERIFICATION_MESSAGE_PREFIX = "핏토링 인증번호는 [";
    private static final String VERIFICATION_MESSAGE_SUFFIX = "] 입니다.";

    public String createSmsReservationMessage(String menteeName, String content) {
        return String.format("""
                        멘토링 신청자: %s
                        상담 내용: %s
                        
                        마이페이지에서 예약을 승인 또는 거절해주세요.
                        """,
                menteeName,
                content
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
