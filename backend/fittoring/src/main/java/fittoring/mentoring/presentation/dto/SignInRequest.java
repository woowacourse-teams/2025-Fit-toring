package fittoring.mentoring.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record SignInRequest(
        @NotBlank String loginId,
        @NotBlank String password
) {
}
