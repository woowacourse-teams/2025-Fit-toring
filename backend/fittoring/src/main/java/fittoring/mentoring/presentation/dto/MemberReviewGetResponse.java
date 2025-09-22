package fittoring.mentoring.presentation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MemberReviewGetResponse(
    Long id,
    LocalDate createdAt,
    int rating,
    String content
) {

    public MemberReviewGetResponse(Long id, LocalDateTime createdAt, int rating, String content) {
        this(id, createdAt.toLocalDate(), rating, content);
    }
}
