package fittoring.mentoring.infra;

import fittoring.mentoring.business.service.dto.SmsReservationMessageDto;
import org.springframework.stereotype.Component;

@Component
public class SmsMessageFormatter {

    private static final String VERIFICATION_MESSAGE_PREFIX = "핏토링 인증번호는 [";
    private static final String VERIFICATION_MESSAGE_SUFFIX = "] 입니다.";

    public String approvedReservationMessage(String mentorName, String context, String mentorPhone) {
        return String.format("""
                        [핏토링] 멘토링 예약이 승인되었습니다.
                        
                        멘토: %s
                        상담 내용: %s
                        상담 시간: 15분
                        멘토 연락처: %s
                        
                        상담 전에 꼭 연락해 주세요!
                        """,
                mentorName,
                context,
                mentorPhone
        );
    }

    public String rejectedReservationMessage(String mentorName) {
        return String.format("""
                        [핏토링] %s님 과의 멘토링이 취소되었습니다.
                        
                        마이페이지에서 자세한 내용을 확인하실 수 있습니다.
                        """,
                mentorName
        );
    }

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
