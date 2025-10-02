package fittoring.application.presentation.dto;

import fittoring.domain.model.Reservation;

public record ReservationCreateResponse(
    String mentorName,
    String menteeName,
    String menteePhone
) {

    public static ReservationCreateResponse from(Reservation savedReservation) {
        return new ReservationCreateResponse(
                savedReservation.getMentorName(),
                savedReservation.getMenteeName(),
                savedReservation.getMenteePhone()
        );
    }
}
