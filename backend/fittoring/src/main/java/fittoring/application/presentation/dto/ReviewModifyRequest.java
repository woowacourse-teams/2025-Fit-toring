package fittoring.application.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ReviewModifyRequest(
    @Min(1)
    @Max(5)
    Integer rating,
    String content
) {

}
