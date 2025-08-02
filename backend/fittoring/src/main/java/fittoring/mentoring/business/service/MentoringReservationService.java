package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.service.dto.ReservationCreateDto;
import fittoring.mentoring.business.service.dto.SmsReservationMessageDto;
import fittoring.mentoring.infra.SmsMessageFormatter;
import fittoring.mentoring.infra.SmsRestClientService;
import fittoring.mentoring.presentation.dto.ReservationCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MentoringReservationService {

    private static final String RESERVATION_SUBJECT = "핏토링 예약 알림";

    private final ReservationService reservationService;
    private final SmsRestClientService smsRestClientService;
    private final SmsMessageFormatter smsMessageFormatter;

    public ReservationCreateResponse reserveMentoring(ReservationCreateDto dto) {
        ReservationCreateResponse response = reservationService.createReservation(dto);
        String smsMessage = smsMessageFormatter.createSmsReservationMessage(SmsReservationMessageDto.of(dto));
        smsRestClientService.sendSms(new Phone(response.mentorPhone()), smsMessage, RESERVATION_SUBJECT);
        return response;
    }
}
