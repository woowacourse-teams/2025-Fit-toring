package fittoring.application.reservation.service.event;

import fittoring.application.mentoring.service.dto.ReservationInfo;
import fittoring.domain.model.Phone;

public record ReservationApprovedEvent(
        Long reservationId,
        String mentorName,
        String content,
        Phone menteePhone,
        String chatRoomUrl
) {
    public static ReservationApprovedEvent of(ReservationInfo info) {
        return new ReservationApprovedEvent(
                info.reservationId(),
                info.mentorName(),
                info.content(),
                info.menteePhone(),
                info.chatRoomUrl()
        );
    }
}
