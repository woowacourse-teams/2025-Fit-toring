package fittoring.mentoring.presentation.dto;

import java.time.LocalDate;

public record MentoringReviewGetResponse(
    Long id,
    String reviewerName,
    LocalDate createdAt,
    int rating,
    String content
) {

}