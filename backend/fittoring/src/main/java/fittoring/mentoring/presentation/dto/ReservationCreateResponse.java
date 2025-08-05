package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.Reservation;

public record ReservationCreateResponse(
        String mentorName,
        String menteeName,
        String mentorPhone,
        String menteePhone,
        String content
) {

    public static ReservationCreateResponse from(Reservation savedReservation) {
        return new ReservationCreateResponse(
                savedReservation.getMentorName(),
                savedReservation.getMenteeName(),
                savedReservation.getMentorPhone(),
                savedReservation.getMenteePhone(),
                savedReservation.getContext()
        );
    }
}
