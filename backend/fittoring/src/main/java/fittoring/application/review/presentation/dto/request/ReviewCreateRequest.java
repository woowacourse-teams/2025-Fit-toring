package fittoring.application.review.presentation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewCreateRequest(
        @NotNull(message = "예약 ID는 필수 입력값입니다.")
        Long reservationId,
        @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
        @Max(value = 5, message = "평점은 5점 이하여야 합니다.")
        @NotNull(message = "평점은 필수 입력값입니다.")
        int rating,
        String content
) {

}
