package fittoring.application.reservation.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReservationCreateRequest(
        @NotBlank(message = "내용은 필수 입력값입니다.")
        String content
) {
}
