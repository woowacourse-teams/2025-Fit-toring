package fittoring.application.auth.presentation.dto.request;

import fittoring.application.auth.service.dto.RegisterMemberDto;
import fittoring.domain.model.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @Size(min = 5, max = 15, message = "아이디는 5자 이상 15자 이하로 입력해주세요.")
        @NotBlank(message = "아이디는 필수 입력값입니다.")
        String loginId,
        @Size(min = 2, max = 5, message = "이름은 2자 이상 5자 이하로 입력해주세요.")
        @NotBlank(message = "이름은 필수 입력값입니다.")
        String name,
        @NotNull(message = "성별은 필수 입력값입니다.")
        Gender gender,
        @PhoneNumber
        @NotBlank(message = "전화번호는 필수 입력값입니다.")
        String phoneNumber,
        @Size(min = 5, max = 20, message = "비밀번호는 5자 이상 20자 이하로 입력해주세요.")
        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        String password
) {
    public RegisterMemberDto toRegisterMemberDto() {
        return new RegisterMemberDto(loginId, name, gender, phoneNumber, password);
    }
}
