package fittoring.mentoring.infra;

import fittoring.mentoring.business.service.dto.SmsReservationMessageDto;
import org.springframework.stereotype.Component;

@Component
public class SmsMessageFormatter {

    public String createSmsReservationMessage(SmsReservationMessageDto response) {
        return String.format("""
                        멘토링 신청자: %s
                        신청자 전화번호: %s\n
                        상담 내용: %s
                        """,
                response.menteeName(),
                response.menteePhone(),
                response.content()
        );
    }
}
