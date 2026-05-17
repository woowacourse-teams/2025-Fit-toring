package fittoring.application.reservation.service;

import fittoring.domain.model.Phone;
import fittoring.domain.model.Reservation;
import fittoring.infrastructure.SmsMessageFormatter;
import fittoring.infrastructure.SmsRestClientService;
import fittoring.infrastructure.exception.SmsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
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
        sendSafely(
                () -> smsRestClientService.sendSms(
                        new Phone(reservation.getMentorPhone()),
                        smsMessage,
                        RESERVATION_SUBJECT
                ),
                "예약 생성",
                reservation.getId()
        );
    }

    public void sendReservationApproveSmsMessage(
            Long reservationId,
            String mentorName,
            String content,
            Phone menteePhoneNumber,
            String chatRoomUrl
    ) {
        String message = smsMessageFormatter.approvedReservationMessage(mentorName, content, chatRoomUrl);
        sendSafely(
                () -> smsRestClientService.sendSms(menteePhoneNumber, message, RESERVATION_SUBJECT),
                "예약 승인",
                reservationId
        );
    }

    public void sendReservationRejectSmsMessage(Long reservationId, String mentorName, Phone menteePhoneNumber) {
        String message = smsMessageFormatter.rejectedReservationMessage(mentorName);
        sendSafely(
                () -> smsRestClientService.sendSms(menteePhoneNumber, message, RESERVATION_SUBJECT),
                "예약 거절",
                reservationId
        );
    }

    private void sendSafely(Runnable smsCall, String operation, Long reservationId) {
        try {
            smsCall.run();
        } catch (SmsException e) {
            log.warn("{} SMS 발송 실패: reservationId={}", operation, reservationId, e);
        }
    }
}
