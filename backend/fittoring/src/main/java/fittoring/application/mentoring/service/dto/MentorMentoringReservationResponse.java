package fittoring.application.mentoring.service.dto;

import fittoring.domain.model.ChatRoom;
import fittoring.domain.model.ChatStatus;
import fittoring.domain.model.Reservation;
import java.time.LocalDateTime;
import java.util.Optional;

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

    public static MentorMentoringReservationResponse of(Reservation reservation, Optional<ChatRoom> chatRoom) {
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
        Optional<ChatRoom> chatRoom
    ) {
        if (chatRoom.isEmpty()) {
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
            chatRoom.get().getId(),
            chatRoom.get().getStatus(),
            reservation.getCreatedAt()
        );
    }
}
