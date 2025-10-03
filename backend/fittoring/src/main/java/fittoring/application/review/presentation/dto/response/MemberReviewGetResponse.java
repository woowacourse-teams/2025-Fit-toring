package fittoring.application.review.presentation.dto.response;

import java.time.LocalDate;

public record MemberReviewGetResponse(
    Long id,
    LocalDate createdAt,
    int rating,
    String content
) {

}
