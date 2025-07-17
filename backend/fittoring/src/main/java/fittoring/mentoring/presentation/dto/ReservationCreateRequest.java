package fittoring.mentoring.presentation.dto;

public record ReservationCreateRequest(
        String menteeName,
        String menteePhone,
        String content
) {
}
