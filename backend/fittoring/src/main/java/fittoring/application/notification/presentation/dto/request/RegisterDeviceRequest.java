package fittoring.application.notification.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterDeviceRequest(
        @NotNull
        Long memberId,
        @NotBlank
        String pushToken
) {
}
