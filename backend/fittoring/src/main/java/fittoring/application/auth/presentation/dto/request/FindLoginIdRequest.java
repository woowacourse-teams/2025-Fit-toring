package fittoring.application.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FindLoginIdRequest(
        @Size(min = 2, max = 5, message = "이름은 2자 이상 5자 이하로 입력해주세요.")
        @NotBlank(message = "이름은 필수 입력값입니다.")
        String name,
        @PhoneNumber
        @NotBlank(message = "전화번호는 필수 입력값입니다.")
        String phoneNumber
) {
}
