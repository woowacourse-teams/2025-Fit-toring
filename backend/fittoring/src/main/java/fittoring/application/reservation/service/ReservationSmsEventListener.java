package fittoring.application.reservation.service;

import fittoring.application.reservation.service.event.ReservationApprovedEvent;
import fittoring.application.reservation.service.event.ReservationCreatedEvent;
import fittoring.application.reservation.service.event.ReservationRejectedEvent;
import fittoring.config.SmsAsyncConfiguration;
import fittoring.domain.model.Phone;
import fittoring.infrastructure.SmsMessageFormatter;
import fittoring.infrastructure.SmsRestClientService;
import fittoring.infrastructure.exception.SmsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationSmsEventListener {

    private static final String RESERVATION_SUBJECT = "핏토링 예약 알림";
    private static final String DESCRIPTION_CREATED = "예약 생성";
    private static final String DESCRIPTION_APPROVED = "예약 승인";
    private static final String DESCRIPTION_REJECTED = "예약 거절";

    private final SmsRestClientService smsRestClientService;
    private final SmsMessageFormatter smsMessageFormatter;

    @Async(SmsAsyncConfiguration.SMS_EXECUTOR_BEAN)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReservationCreated(ReservationCreatedEvent event) {
        String message = smsMessageFormatter.reservationMessage(event.menteeName(), event.content());
        sendSafely(event.mentorPhone(), message, DESCRIPTION_CREATED, event.reservationId());
    }

    @Async(SmsAsyncConfiguration.SMS_EXECUTOR_BEAN)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReservationApproved(ReservationApprovedEvent event) {
        String message = smsMessageFormatter.approvedReservationMessage(
                event.mentorName(),
                event.content(),
                event.chatRoomUrl()
        );
        sendSafely(event.menteePhone(), message, DESCRIPTION_APPROVED, event.reservationId());
    }

    @Async(SmsAsyncConfiguration.SMS_EXECUTOR_BEAN)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReservationRejected(ReservationRejectedEvent event) {
        String message = smsMessageFormatter.rejectedReservationMessage(event.mentorName());
        sendSafely(event.menteePhone(), message, DESCRIPTION_REJECTED, event.reservationId());
    }

    private void sendSafely(Phone toPhone, String message, String description, Long reservationId) {
        try {
            smsRestClientService.sendSms(toPhone, message, RESERVATION_SUBJECT);
        } catch (SmsException e) {
            log.warn("{} SMS 발송 실패: reservationId={}", description, reservationId, e);
        }
    }
}
