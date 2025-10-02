package fittoring.application.presentation.dto;

import fittoring.application.business.service.dto.RatingStatsDto;
import java.util.List;

public record AdminReviewInfoResponse(
        String ratingAverage,
        long ratingCount,
        List<AdminReviewResponse> reviewData
) {

    public static AdminReviewInfoResponse of(List<AdminReviewResponse> reviews, RatingStatsDto ratingStats) {
        return new AdminReviewInfoResponse(
                String.format("%.1f", ratingStats.average()),
                ratingStats.count(),
                reviews
        );
    }
}
