package fittoring.mentoring.presentation.dto;

import java.time.LocalDateTime;

public record MentoringReviewGetResponse(
    Long id,
    String reviewerName,
    LocalDateTime createdAt,
    int rating,
    String content
) {

}
