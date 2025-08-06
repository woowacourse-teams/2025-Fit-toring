package fittoring.mentoring.business.service.dto;

import fittoring.mentoring.presentation.dto.ReservationCreateResponse;

public record SmsReservationMessageDto(
        String mentorName,
        String mentorPhone,
        String content
) {

    public static SmsReservationMessageDto of(ReservationCreateResponse dto) {
        return new SmsReservationMessageDto(
                dto.mentorName(),
                dto.mentorPhone(),
                dto.content()
        );
    }
}
