package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.presentation.PhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(

        @Size(min = 4, max = 20, message = "아이디는 5자 이상 15자 이하로 입력해주세요.")
        @NotBlank
        String loginId,

        @NotBlank
        @Size(min = 2, max = 5, message = "이름은 2자 이상 5자 이하로 입력해주세요.")
        String name,

        @NotBlank
        String male,

        @PhoneNumber
        @NotBlank
        String phone,

        @Size(min = 5, max = 20, message = "비밀번호는 5자 이상 20자 이하로 입력해주세요.")
        @NotBlank
        String password
) {

    public Member toEntity() {
        return new Member(loginId, name, male, phone, password);
    }
}
