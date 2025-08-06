package fittoring.mentoring.business.service.dto;

import fittoring.mentoring.presentation.dto.ReviewCreateRequest;

public record ReviewCreateDto(
    Long menteeId,
    Long mentoringId,
    Long reservationId,
    int rating,
    String content
) {

    public static ReviewCreateDto of(Long menteeId, Long mentoringId, ReviewCreateRequest reviewCreateRequest) {
        return new ReviewCreateDto(
            menteeId,
            mentoringId,
            reviewCreateRequest.reservationId(),
            reviewCreateRequest.rating(),
            reviewCreateRequest.content()
        );
    }
}