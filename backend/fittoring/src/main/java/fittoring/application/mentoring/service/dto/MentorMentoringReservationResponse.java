package fittoring.application.mentoring.service.dto;

import fittoring.domain.model.ChatRoom;
import fittoring.domain.model.ChatStatus;
import fittoring.domain.model.Reservation;
import java.time.LocalDateTime;

public record MentorMentoringReservationResponse(
        Long reservationId,
        String menteeName,
        String phoneNumber,
        int price,
        String content,
        String status,
        Long chatRoomId,
        ChatStatus chatStatus,
        LocalDateTime createdAt
) {

    public static MentorMentoringReservationResponse of(Reservation reservation) {
        return of(reservation, null);
    }

    public static MentorMentoringReservationResponse of(Reservation reservation, ChatRoom chatRoom) {
        if (reservation.isPending()) {
            return ofPending(reservation);
        }
        return ofApprovedOrRejected(reservation, chatRoom);
    }

    private static MentorMentoringReservationResponse ofPending(Reservation reservation) {
        return new MentorMentoringReservationResponse(
                reservation.getId(),
                reservation.getMenteeName(),
                null,
                reservation.getMentoring().getPrice(),
                reservation.getContent(),
                reservation.getStatus(),
                null,
                null,
                reservation.getCreatedAt()
        );
    }

    private static MentorMentoringReservationResponse ofApprovedOrRejected(
        Reservation reservation,
        ChatRoom chatRoom
    ) {
        if (chatRoom == null) {
            return new MentorMentoringReservationResponse(
                reservation.getId(),
                reservation.getMenteeName(),
                reservation.getMenteePhone(),
                reservation.getMentoring().getPrice(),
                reservation.getContent(),
                reservation.getStatus(),
                null,
                null,
                reservation.getCreatedAt()
            );
        }
        return new MentorMentoringReservationResponse(
            reservation.getId(),
            reservation.getMenteeName(),
            reservation.getMenteePhone(),
            reservation.getMentoring().getPrice(),
            reservation.getContent(),
            reservation.getStatus(),
            chatRoom.getId(),
            chatRoom.getStatus(),
            reservation.getCreatedAt()
        );
    }
}
