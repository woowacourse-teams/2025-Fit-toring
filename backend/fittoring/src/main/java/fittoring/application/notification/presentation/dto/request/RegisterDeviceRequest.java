package fittoring.application.notification.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterDeviceRequest(
        @NotNull(message = "회원 ID는 필수 입력값입니다.")
        Long memberId,
        @NotBlank(message = "푸시 토큰은 필수 입력값입니다.")
        String pushToken
) {
}
