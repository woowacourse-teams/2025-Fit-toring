package fittoring.application.auth.presentation.dto.request;

import fittoring.domain.model.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OauthSignUpRequest(
        @NotBlank(message = "이름은 필수 입력값입니다.")
        String name,
        @NotNull(message = "성별은 필수 입력값입니다.")
        Gender gender,
        @PhoneNumber
        @NotBlank(message = "전화번호는 필수 입력값입니다.")
        String phone) {

}
