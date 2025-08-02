package fittoring.mentoring.presentation.dto;

public record MentoringReviewGetResponse(
    Long id,
    String reviewerName,
    byte rating,
    String content
) {

}
