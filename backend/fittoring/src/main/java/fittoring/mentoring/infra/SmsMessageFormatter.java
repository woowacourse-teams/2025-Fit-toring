package fittoring.mentoring.infra;

import fittoring.mentoring.business.service.dto.SmsReservationMessageDto;
import org.springframework.stereotype.Component;

@Component
public class SmsMessageFormatter {

    private static final String VERIFICATION_MESSAGE_PREFIX = "핏토링 인증번호는 [";
    private static final String VERIFICATION_MESSAGE_SUFFIX = "] 입니다.";

    public String createApproveReservationMessage(String mentorName, String context, String mentorPhone) {
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

    public String createRejectReservationMessage(String mentorName, String context) {
        return String.format("""
                        [핏토링] 멘토링 예약이 거절되었습니다.
                        
                        멘토: %s
                        상담 내용: %s
                        
                        아쉽게도 이번 예약은 진행되지 않게 되었어요.
                        다른 멘토링을 신청해 보시는 건 어떨까요?
                        """,
                mentorName,
                context
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
