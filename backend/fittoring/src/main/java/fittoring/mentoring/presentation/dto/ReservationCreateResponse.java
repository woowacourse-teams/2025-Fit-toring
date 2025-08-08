package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.Reservation;

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
