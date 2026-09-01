package fittoring.application.chat.presentation.dto.request;

import fittoring.domain.model.ImageExtension;
import jakarta.validation.constraints.NotNull;

public record ChatImagePresignedRequest(
        @NotNull(message = "확장자는 필수 입력값입니다.")
        ImageExtension extension
) {
}
