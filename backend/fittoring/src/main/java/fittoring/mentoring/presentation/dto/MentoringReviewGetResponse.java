package fittoring.mentoring.presentation.dto;

public record MentoringReviewGetResponse(
    Long id,
    String reviewerName,
    int rating,
    String content
) {

}