package fittoring.mentoring.business.service.dto;

import fittoring.mentoring.presentation.dto.ReviewCreateRequest;

public record ReviewCreateDto(
    Long menteeId,
    Long reservationId,
    int rating,
    String content
) {

    public static ReviewCreateDto of(Long menteeId, ReviewCreateRequest reviewCreateRequest) {
        return new ReviewCreateDto(
            menteeId,
            reviewCreateRequest.reservationId(),
            reviewCreateRequest.rating(),
            reviewCreateRequest.content()
        );
    }
}