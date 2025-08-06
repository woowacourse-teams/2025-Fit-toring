package fittoring.mentoring.business.service.dto;

import fittoring.mentoring.presentation.dto.ReservationCreateResponse;

public record SmsReservationMessageDto(
        String mentorName,
        String content
) {

    public static SmsReservationMessageDto of(ReservationCreateResponse dto) {
        return new SmsReservationMessageDto(
                reservation.getMentorName(),
                reservation.getContext()
        );
    }
}
