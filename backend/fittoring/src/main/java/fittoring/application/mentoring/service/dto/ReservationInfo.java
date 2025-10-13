package fittoring.application.mentoring.service.dto;


import fittoring.domain.model.Reservation;

public record ReservationInfo(
        Reservation reservation,
        String chatRoomUrl
) {
}
