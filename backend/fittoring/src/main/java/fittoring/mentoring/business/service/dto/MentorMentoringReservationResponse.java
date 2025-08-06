package fittoring.mentoring.business.service.dto;

import fittoring.mentoring.business.model.Reservation;
import java.time.LocalDateTime;

public record MentorMentoringReservationResponse(
        Long reservationId,
        String menteeName,
        String phoneNumber,
        int price,
        String content,
        String status,
        LocalDateTime createdAt
) {

    public static MentorMentoringReservationResponse of(Reservation reservation) {
        if (reservation.isPending()) {
            return ofPending(reservation);
        }
        return ofApprovedOrRejected(reservation);
    }

    private static MentorMentoringReservationResponse ofPending(Reservation reservation) {
        return new MentorMentoringReservationResponse(
                reservation.getId(),
                reservation.getMenteeName(),
                null,
                reservation.getMentoring().getPrice(),
                reservation.getMentoring().getContent(),
                reservation.getStatus(),
                reservation.getCreatedAt()
        );
    }

    private static MentorMentoringReservationResponse ofApprovedOrRejected(Reservation reservation) {
        return new MentorMentoringReservationResponse(
                reservation.getId(),
                reservation.getMenteeName(),
                reservation.getMenteePhone(),
                reservation.getMentoring().getPrice(),
                reservation.getMentoring().getContent(),
                reservation.getStatus(),
                reservation.getCreatedAt()
        );
    }
}
