package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.Review;

public record ReviewCreateResponse(
    byte rating,
    String content
) {

    public static ReviewCreateResponse of(Review savedReview) {
        return new ReviewCreateResponse(savedReview.getRating(), savedReview.getContent());
    }
}
