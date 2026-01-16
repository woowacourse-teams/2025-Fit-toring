package fittoring.application.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ValidateDuplicateLoginIdRequest(
        @NotBlank(message = "아이디는 필수 입력값입니다.")
        String loginId
) {

}
