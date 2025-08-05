package fittoring.mentoring.business.service.dto;

import fittoring.mentoring.presentation.dto.ReservationCreateRequest;

public record ReservationCreateDto(
        Long menteeId,
        Long mentoringId,
        String content
) {

    public static ReservationCreateDto of(Long menteeId, Long mentoringId, ReservationCreateRequest request) {
        return new ReservationCreateDto(
                menteeId,
                mentoringId,
                request.content()
        );
    }
}
