package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.model.Status;
import fittoring.mentoring.business.service.dto.SmsReservationMessageDto;
import fittoring.mentoring.infra.SmsMessageFormatter;
import fittoring.mentoring.infra.SmsRestClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReservationNotificationService {

    private static final String RESERVATION_SUBJECT = "핏토링 예약 알림";

    private final SmsRestClientService smsRestClientService;
    private final SmsMessageFormatter smsMessageFormatter;

    public void sendReservationSmsMessage(Reservation reservation) {
        String smsMessage = smsMessageFormatter.reservationMessage(SmsReservationMessageDto.of(reservation));
        smsRestClientService.sendSms(
                new Phone(reservation.getMentee().getPhoneNumber()),
                smsMessage,
                RESERVATION_SUBJECT
        );
    }

    public void sendReservationStatusUpdateSmsMessage(Reservation reservation, String updateStatus) {
        Status status = Status.of(updateStatus);
        String mentorName = reservation.getMentorName();
        String context = reservation.getContext();
        String mentorPhoneNumber = reservation.getMentorPhone();

        if (status.isNotifiable()) {
            String message = createMessage(status, mentorName, context, mentorPhoneNumber);
            Phone menteePhone = reservation.getMentee().getPhone();
            smsRestClientService.sendSms(menteePhone, message, RESERVATION_SUBJECT);
        }
    }

    private String createMessage(Status updateStatus, String mentorName, String context, String mentorPhoneNumber) {
        if (updateStatus.isApprove()) {
            return smsMessageFormatter.approvedReservationMessage(
                    mentorName,
                    context,
                    mentorPhoneNumber
            );
        }
        return smsMessageFormatter.rejectedReservationMessage(mentorName);
    }

}
