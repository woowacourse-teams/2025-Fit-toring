package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.model.Status;
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
        Reservation reservation = reservationService.createReservation(dto);
        String smsMessage = smsMessageFormatter.createSmsReservationMessage(SmsReservationMessageDto.of(reservation));
        smsRestClientService.sendSms(
                new Phone(reservation.getMentee().getPhoneNumber()),
                smsMessage,
                RESERVATION_SUBJECT
        );
        return ReservationCreateResponse.from(reservation);
    }

    public void updateStatusAndSendSms(Long reservationId, String status) {
        Reservation reservation = reservationService.updateStatus(reservationId, status);
        sendSms(reservation, status);
    }

    private void sendSms(Reservation reservation, String updateStatus) {
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

    private String createMessage(Status status, String mentorName, String context, String mentorPhoneNumber) {
        if (status.isApprove()) {
            return smsMessageFormatter.createApproveReservationMessage(
                    mentorName,
                    context,
                    mentorPhoneNumber
            );
        }
        return smsMessageFormatter.createRejectReservationMessage(mentorName, context);
    }

}
