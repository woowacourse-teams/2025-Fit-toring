package fittoring.application.reservation.sms;

import fittoring.application.reservation.repository.SmsOutboxRepository;
import fittoring.application.reservation.service.event.ReservationApprovedEvent;
import fittoring.application.reservation.service.event.ReservationCreatedEvent;
import fittoring.application.reservation.service.event.ReservationRejectedEvent;
import fittoring.infrastructure.sms.SmsMessageFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationSmsOutboxWriteListener {

    private static final String RESERVATION_SUBJECT = "핏토링 예약 알림";

    private final SmsOutboxRepository smsOutboxRepository;
    private final SmsMessageFormatter smsMessageFormatter;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    public void onReservationCreated(ReservationCreatedEvent event) {
        String message = smsMessageFormatter.reservationMessage(event.menteeName(), event.content());
        saveAndPublish(SmsOutbox.pending(
                event.reservationId(),
                SmsOutboxEventType.RESERVATION_CREATED,
                event.mentorPhone(),
                message,
                RESERVATION_SUBJECT
        ));
    }

    @EventListener
    public void onReservationApproved(ReservationApprovedEvent event) {
        String message = smsMessageFormatter.approvedReservationMessage(
                event.mentorName(),
                event.content(),
                event.chatRoomUrl()
        );
        saveAndPublish(SmsOutbox.pending(
                event.reservationId(),
                SmsOutboxEventType.RESERVATION_APPROVED,
                event.menteePhone(),
                message,
                RESERVATION_SUBJECT
        ));
    }

    @EventListener
    public void onReservationRejected(ReservationRejectedEvent event) {
        String message = smsMessageFormatter.rejectedReservationMessage(event.mentorName());
        saveAndPublish(SmsOutbox.pending(
                event.reservationId(),
                SmsOutboxEventType.RESERVATION_REJECTED,
                event.menteePhone(),
                message,
                RESERVATION_SUBJECT
        ));
    }

    private void saveAndPublish(SmsOutbox outbox) {
        SmsOutbox saved = smsOutboxRepository.save(outbox);
        // 커밋 후 SQS 빠른 길 발행을 트리거 (AFTER_COMMIT 리스너가 수신)
        eventPublisher.publishEvent(new SmsOutboxCreatedEvent(saved.getId()));
    }
}
