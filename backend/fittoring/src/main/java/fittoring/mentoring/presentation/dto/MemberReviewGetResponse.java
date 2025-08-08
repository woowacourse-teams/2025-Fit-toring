package fittoring.mentoring.presentation.dto;

import java.time.LocalDate;

public record MemberReviewGetResponse(
    Long id,
    LocalDate createdAt,
    int rating,
    String content
) {

}