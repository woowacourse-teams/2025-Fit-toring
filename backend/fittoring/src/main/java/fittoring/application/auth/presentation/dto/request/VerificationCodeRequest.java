package fittoring.application.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerificationCodeRequest(
        @PhoneNumber
        @NotBlank(message = "전화번호는 필수 입력값입니다.")
        String phoneNumber,
        @NotBlank(message = "인증 코드는 필수 입력값입니다.")
        String code
) {

}
