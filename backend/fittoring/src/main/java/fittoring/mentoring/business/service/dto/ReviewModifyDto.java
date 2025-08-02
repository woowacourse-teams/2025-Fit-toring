package fittoring.mentoring.business.service.dto;

import fittoring.mentoring.presentation.dto.ReviewModifyRequest;

public record ReviewModifyDto(
    Long reviewerId,
    Long reviewId,
    byte rating,
    String content
) {

    public static ReviewModifyDto of(Long reviewerId, Long reviewId, ReviewModifyRequest reviewModifyRequest) {
        return new ReviewModifyDto(
            reviewerId,
            reviewId,
            reviewModifyRequest.rating(),
            reviewModifyRequest.content()
        );
    }
}
