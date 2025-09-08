package fittoring.mentoring.presentation.dto;

import java.time.LocalDate;

public record ReviewGetResponse(
    Long id,
    String reviewerName,
    LocalDate createdAt,
    int rating,
    String content
) {

}
