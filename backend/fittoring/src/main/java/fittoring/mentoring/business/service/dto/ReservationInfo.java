package fittoring.mentoring.business.service.dto;

import fittoring.mentoring.business.model.Reservation;

public record ReservationInfo(
        Reservation reservation,
        String chatRoomUrl
) {
}
