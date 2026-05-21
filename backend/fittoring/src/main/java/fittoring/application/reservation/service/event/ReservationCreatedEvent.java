package fittoring.application.reservation.service.event;

import fittoring.domain.model.Phone;
import fittoring.domain.model.Reservation;

public record ReservationCreatedEvent(
        Long reservationId,
        String menteeName,
        String content,
        Phone mentorPhone
) {
    public static ReservationCreatedEvent of(Reservation reservation) {
        return new ReservationCreatedEvent(
                reservation.getId(),
                reservation.getMenteeName(),
                reservation.getContent(),
                new Phone(reservation.getMentorPhone())
        );
    }
}
