package fittoring.application.reservation.service;

import fittoring.application.reservation.service.event.ReservationApprovedEvent;
import fittoring.application.reservation.service.event.ReservationCreatedEvent;
import fittoring.application.reservation.service.event.ReservationRejectedEvent;
import fittoring.domain.model.Phone;
import fittoring.domain.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReservationNotificationService {

    private final ApplicationEventPublisher eventPublisher;

    public void sendReservationSmsMessage(Reservation reservation) {
        eventPublisher.publishEvent(new ReservationCreatedEvent(
                reservation.getId(),
                reservation.getMenteeName(),
                reservation.getContent(),
                new Phone(reservation.getMentorPhone())
        ));
    }

    public void sendReservationApproveSmsMessage(
            Long reservationId,
            String mentorName,
            String content,
            Phone menteePhoneNumber,
            String chatRoomUrl
    ) {
        eventPublisher.publishEvent(new ReservationApprovedEvent(
                reservationId,
                mentorName,
                content,
                menteePhoneNumber,
                chatRoomUrl
        ));
    }

    public void sendReservationRejectSmsMessage(Long reservationId, String mentorName, Phone menteePhoneNumber) {
        eventPublisher.publishEvent(new ReservationRejectedEvent(
                reservationId,
                mentorName,
                menteePhoneNumber
        ));
    }
}
