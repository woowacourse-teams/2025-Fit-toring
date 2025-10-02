package fittoring.application.presentation.dto;

import fittoring.application.presentation.PhoneNumber;
import jakarta.validation.constraints.NotBlank;

public record VerificationCodeRequest(
        @PhoneNumber
        @NotBlank
        String phone,
        @NotBlank
        String code
) {

}
