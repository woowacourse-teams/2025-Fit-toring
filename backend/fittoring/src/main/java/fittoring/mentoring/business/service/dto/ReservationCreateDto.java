package fittoring.mentoring.business.service.dto;

import fittoring.mentoring.presentation.dto.ReservationCreateRequest;

public record ReservationCreateDto(
        Long mentoringId,
        String menteeName,
        String menteePhone,
        String content
) {

    public static ReservationCreateDto of(Long mentoringId, ReservationCreateRequest request) {
        return new ReservationCreateDto(
                mentoringId,
                request.menteeName(),
                request.menteePhone(),
                request.content()
        );
    }
}
