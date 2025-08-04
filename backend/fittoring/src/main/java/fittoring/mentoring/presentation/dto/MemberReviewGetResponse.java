package fittoring.mentoring.presentation.dto;

public record MemberReviewGetResponse(
    Long id,
    Long mentoringId,
    int rating,
    String content
) {

}
