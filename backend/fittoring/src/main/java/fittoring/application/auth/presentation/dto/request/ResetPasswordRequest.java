package fittoring.application.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @Size(min = 5, max = 15, message = "아이디는 5자 이상 15자 이하로 입력해주세요.")
        @NotBlank(message = "아이디는 필수 입력값입니다.")
        String loginId,
        @PhoneNumber
        @NotBlank(message = "전화번호는 필수 입력값입니다.")
        String phoneNumber,
        @Size(min = 5, max = 20, message = "비밀번호는 5자 이상 20자 이하로 입력해주세요.")
        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        String password
) {
}
