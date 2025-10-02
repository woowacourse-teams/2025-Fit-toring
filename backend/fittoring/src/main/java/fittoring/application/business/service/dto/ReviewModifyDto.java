package fittoring.application.business.service.dto;

import fittoring.application.presentation.dto.ReviewModifyRequest;

public record ReviewModifyDto(
    Long menteeId,
    Long reviewId,
    Integer rating,
    String content
) {

    public static ReviewModifyDto of(Long menteeId, Long reviewId, ReviewModifyRequest reviewModifyRequest) {
        return new ReviewModifyDto(
            menteeId,
            reviewId,
            reviewModifyRequest.rating(),
            reviewModifyRequest.content()
        );
    }
}
