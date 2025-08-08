package fittoring.mentoring.business.service.dto;

public record ReservationMenteeSmsDto(
        String menteeName,
        String content
) {

    public static ReservationMenteeSmsDto of(String menteeName, String content) {
        return new ReservationMenteeSmsDto(
                menteeName,
                content
        );
    }
}
