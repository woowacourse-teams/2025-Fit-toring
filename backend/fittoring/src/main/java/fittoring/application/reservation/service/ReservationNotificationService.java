package fittoring.application.reservation.service;

import fittoring.domain.model.Phone;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Status;
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

    public void sendReservationStatusUpdateSmsMessage(
            Reservation reservation,
            String updateStatus,
            String chatRoomUrl
    ) {
        Status status = Status.of(updateStatus);
        String mentorName = reservation.getMentorName();
        String context = reservation.getContent();

        if (status.isNotifiable()) {
            String message = createMessage(status, mentorName, context, chatRoomUrl);
            Phone menteePhone = reservation.getMentee().getPhone();
            smsRestClientService.sendSms(menteePhone, message, RESERVATION_SUBJECT);
        }
    }

    private String createMessage(
            Status updateStatus,
            String mentorName,
            String context,
            String chatRoomUrl
    ) {
        if (updateStatus.isApprove()) {
            return smsMessageFormatter.approvedReservationMessage(
                    mentorName,
                    context,
                    chatRoomUrl
            );
        }
        return smsMessageFormatter.rejectedReservationMessage(mentorName);
    }
}
