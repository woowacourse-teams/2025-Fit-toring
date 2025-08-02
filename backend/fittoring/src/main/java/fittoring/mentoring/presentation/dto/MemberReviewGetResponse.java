package fittoring.mentoring.presentation.dto;

public record MemberReviewGetResponse(
    Long id,
    Long mentoringId,
    byte rating,
    String content
) {

}
