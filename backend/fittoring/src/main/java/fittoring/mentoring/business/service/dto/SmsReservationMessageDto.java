package fittoring.mentoring.business.service.dto;

import fittoring.mentoring.business.model.Reservation;

public record SmsReservationMessageDto(
        String mentorName,
        String content
) {

    public static SmsReservationMessageDto of(Reservation reservation) {
        return new SmsReservationMessageDto(
                reservation.getMentorName(),
                reservation.getContext()
        );
    }
}
