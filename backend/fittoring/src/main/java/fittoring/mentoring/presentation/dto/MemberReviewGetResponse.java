package fittoring.mentoring.presentation.dto;

import java.time.LocalDateTime;

public record MemberReviewGetResponse(
    Long id,
    Long mentoringId,
    LocalDateTime createdAt,
    int rating,
    String content
) {

}
