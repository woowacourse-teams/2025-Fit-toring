package fittoring.application.reservation.service.event;

import fittoring.domain.model.Phone;

public record ReservationCreatedEvent(
        Long reservationId,
        String menteeName,
        String content,
        Phone mentorPhone
) {
}
