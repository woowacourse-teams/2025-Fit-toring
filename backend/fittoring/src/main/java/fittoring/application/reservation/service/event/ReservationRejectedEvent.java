package fittoring.application.reservation.service.event;

import fittoring.application.mentoring.service.dto.ReservationInfo;
import fittoring.domain.model.Phone;
import lombok.NonNull;

public record ReservationRejectedEvent(
        Long reservationId,
        String mentorName,
        Phone menteePhone
) {
    public static ReservationRejectedEvent of(ReservationInfo info) {
        return new ReservationRejectedEvent(
                info.reservationId(),
                info.mentorName(),
                info.menteePhone()
        );
    }
}
