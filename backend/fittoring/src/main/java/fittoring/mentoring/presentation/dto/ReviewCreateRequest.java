package fittoring.mentoring.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ReviewCreateRequest(
    Long reservationId,
    @Min(1) @Max(5) int rating,
    String content
) {

}
