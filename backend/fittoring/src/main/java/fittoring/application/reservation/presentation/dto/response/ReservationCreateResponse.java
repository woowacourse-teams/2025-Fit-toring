package fittoring.application.reservation.presentation.dto.response;

import fittoring.domain.model.Reservation;

public record ReservationCreateResponse(
        String mentorName,
        String menteeName,
        String menteePhoneNumber
) {

    public static ReservationCreateResponse from(Reservation savedReservation) {
        return new ReservationCreateResponse(
                savedReservation.getMentorName(),
                savedReservation.getMenteeName(),
                savedReservation.getMenteePhone()
        );
    }
}
