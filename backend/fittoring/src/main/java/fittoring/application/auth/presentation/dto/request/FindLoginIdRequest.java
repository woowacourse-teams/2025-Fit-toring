package fittoring.application.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FindLoginIdRequest(
        @NotBlank
        String name,
        @NotBlank
        String phoneNumber
) {
}
