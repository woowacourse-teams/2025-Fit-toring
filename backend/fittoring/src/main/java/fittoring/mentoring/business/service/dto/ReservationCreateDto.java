package fittoring.mentoring.business.service.dto;

public record ReservationCreateDto(
        Long mentoringId,
        String menteeName,
        String menteePhone,
        String content
) {
}
