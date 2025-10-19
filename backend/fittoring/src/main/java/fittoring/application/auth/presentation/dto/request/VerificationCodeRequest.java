package fittoring.application.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerificationCodeRequest(
        @PhoneNumber
        @NotBlank
        String phone,
        @NotBlank
        String code
) {

}
