package fittoring.application.reservation.service;

import fittoring.application.reservation.repository.SmsOutboxRepository;
import fittoring.application.reservation.service.event.ReservationApprovedEvent;
import fittoring.application.reservation.service.event.ReservationCreatedEvent;
import fittoring.application.reservation.service.event.ReservationRejectedEvent;
import fittoring.domain.model.SmsOutbox;
import fittoring.domain.model.SmsOutboxEventType;
import fittoring.infrastructure.SmsMessageFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationSmsOutboxWriteListener {

    private static final String RESERVATION_SUBJECT = "핏토링 예약 알림";

    private final SmsOutboxRepository smsOutboxRepository;
    private final SmsMessageFormatter smsMessageFormatter;

    @EventListener
    public void onReservationCreated(ReservationCreatedEvent event) {
        String message = smsMessageFormatter.reservationMessage(event.menteeName(), event.content());
        smsOutboxRepository.save(SmsOutbox.pending(
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
        smsOutboxRepository.save(SmsOutbox.pending(
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
        smsOutboxRepository.save(SmsOutbox.pending(
                event.reservationId(),
                SmsOutboxEventType.RESERVATION_REJECTED,
                event.menteePhone(),
                message,
                RESERVATION_SUBJECT
        ));
    }
}
