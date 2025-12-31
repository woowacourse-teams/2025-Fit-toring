package fittoring.application.reservation.service;

import fittoring.domain.model.Phone;
import fittoring.domain.model.Reservation;
import fittoring.infrastructure.SmsMessageFormatter;
import fittoring.infrastructure.SmsRestClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReservationNotificationService {

    private static final String RESERVATION_SUBJECT = "핏토링 예약 알림";

    private final SmsRestClientService smsRestClientService;
    private final SmsMessageFormatter smsMessageFormatter;

    public void sendReservationSmsMessage(Reservation reservation) {
        String smsMessage = smsMessageFormatter.reservationMessage(
                reservation.getMenteeName(),
                reservation.getContent()
        );
        smsRestClientService.sendSms(
                new Phone(reservation.getMentorPhone()),
                smsMessage,
                RESERVATION_SUBJECT
        );
    }

    public void sendReservationApproveSmsMessage(
            String mentorName,
            String content,
            Phone menteePhoneNumber,
            String chatRoomUrl
    ) {
        String message = smsMessageFormatter.approvedReservationMessage(mentorName, content, chatRoomUrl);
        smsRestClientService.sendSms(menteePhoneNumber, message, RESERVATION_SUBJECT);
    }

    public void sendReservationRejectSmsMessage(String mentorName, Phone menteePhoneNumber) {
        String message = smsMessageFormatter.rejectedReservationMessage(mentorName);
        smsRestClientService.sendSms(menteePhoneNumber, message, RESERVATION_SUBJECT);
    }
}
