package fittoring.application.community.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GuestPasswordRequest(
        @NotBlank(message = "비밀번호는 필수 입력값입니다.") String guestPassword
) {
}
