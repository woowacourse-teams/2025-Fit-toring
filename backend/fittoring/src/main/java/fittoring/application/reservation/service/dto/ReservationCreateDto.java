package fittoring.application.reservation.service.dto;

import fittoring.application.reservation.presentation.dto.request.ReservationCreateRequest;

public record ReservationCreateDto(
    Long menteeId,
    Long mentoringId,
    String content
) {

    public static ReservationCreateDto of(
        Long menteeId,
        Long mentoringId,
        ReservationCreateRequest request
    ) {
        return new ReservationCreateDto(
                menteeId,
                mentoringId,
                request.content()
        );
    }
}
