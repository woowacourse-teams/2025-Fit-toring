package fittoring.application.mentoring.service.dto;


import fittoring.domain.model.Phone;
import fittoring.domain.model.Reservation;

public record ReservationInfo(
        Long reservationId,
        String mentorName,
        String menteeName,
        Phone menteePhone,
        String content,
        String chatRoomUrl
) {

    public static ReservationInfo from(Reservation reservation, String chatRoomUrl) {
        return new ReservationInfo(
                reservation.getId(),
                reservation.getMentorName(),
                reservation.getMenteeName(),
                reservation.getMentee().getPhone(),
                reservation.getContent(),
                chatRoomUrl
        );
    }
}
