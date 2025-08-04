package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.presentation.PhoneNumber;
import jakarta.validation.constraints.NotBlank;

public record VerificationCodeRequest(
        @PhoneNumber @NotBlank String phone,
        @NotBlank String code
) {
}
