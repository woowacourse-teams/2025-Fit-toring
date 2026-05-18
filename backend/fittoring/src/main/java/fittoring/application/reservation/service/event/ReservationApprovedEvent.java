package fittoring.application.reservation.service.event;

import fittoring.domain.model.Phone;

public record ReservationApprovedEvent(
        Long reservationId,
        String mentorName,
        String content,
        Phone menteePhone,
        String chatRoomUrl
) {
}
