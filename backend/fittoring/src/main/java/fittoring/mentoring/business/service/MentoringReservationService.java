package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.service.dto.ReservationCreateDto;
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

    public ReservationCreateResponse reserveMentoring(ReservationCreateDto request) {
        Reservation reservation = reservationService.createReservation(request);
        String smsMessage = createSmsMessageForMentor(reservation.getMenteeName(), reservation.getContent());

        sendSms(getMentorPhoneNumber(reservation), smsMessage);

        return ReservationCreateResponse.from(reservation);
    }

    private String createSmsMessageForMentor(String menteeName, String content) {
        return smsMessageFormatter.createSmsReservationMessage(menteeName, content);
    }

    private String getMentorPhoneNumber(Reservation reservation) {
        return reservation.getMentoring().getMentorPhoneNumber();
    }

    private void sendSms(String mentorPhoneNumber, String smsMessage) {
        smsRestClientService.sendSms(
                new Phone(mentorPhoneNumber),
                smsMessage,
                RESERVATION_SUBJECT
        );
    }
}
