package fittoring.mentoring.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record ValidateDuplicateIdRequest(
        @NotBlank String id
) {
}
