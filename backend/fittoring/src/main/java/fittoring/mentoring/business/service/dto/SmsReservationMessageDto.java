package fittoring.mentoring.business.service.dto;

public record SmsReservationMessageDto(
        String menteeName,
        String menteePhone,
        String content
) {

    public static SmsReservationMessageDto of(ReservationCreateDto dto) {
        return new SmsReservationMessageDto(
                dto.menteeName(),
                dto.menteePhone(),
                dto.content()
        );
    }
}
