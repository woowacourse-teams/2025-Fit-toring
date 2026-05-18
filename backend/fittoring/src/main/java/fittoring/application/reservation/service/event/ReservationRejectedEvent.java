package fittoring.application.reservation.service.event;

import fittoring.domain.model.Phone;

public record ReservationRejectedEvent(
        Long reservationId,
        String mentorName,
        Phone menteePhone
) {
}
