package fittoring.mentoring.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewCreateRequest(
    Long reservationId,
    @Min(1) @Max(5) @NotNull int rating,
    String content
) {

}
