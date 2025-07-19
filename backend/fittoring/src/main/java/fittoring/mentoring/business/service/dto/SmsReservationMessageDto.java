package fittoring.mentoring.business.service.dto;

public record SmsReservationMessageDto(
        String menteeName,
        String menteePhone,
        String content
) {
}
