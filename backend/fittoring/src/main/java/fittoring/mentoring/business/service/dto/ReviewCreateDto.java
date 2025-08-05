package fittoring.mentoring.business.service.dto;

import fittoring.mentoring.presentation.dto.ReviewCreateRequest;

public record ReviewCreateDto(
    Long reviewerId,
    Long mentoringId,
    int rating,
    String content
) {

    public static ReviewCreateDto of(Long reviewerId, Long mentoringId, ReviewCreateRequest reviewCreateRequest) {
        return new ReviewCreateDto(
            reviewerId,
            mentoringId,
            reviewCreateRequest.rating(),
            reviewCreateRequest.content()
        );
    }
}