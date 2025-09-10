package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Review;
import java.time.LocalDateTime;

public record AdminReviewResponse(
        Long id,
        Long menteeId,
        String menteeName,
        int rating,
        String content,
        LocalDateTime createdAt
) {

    public static AdminReviewResponse of(Review review, Member mentee) {
        return new AdminReviewResponse(
                review.getId(),
                mentee.getId(),
                mentee.getName(),
                review.getRating(),
                review.getContent(),
                review.getCreatedAt()
        );
    }
}
