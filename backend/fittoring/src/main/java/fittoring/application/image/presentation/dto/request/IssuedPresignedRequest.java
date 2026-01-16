package fittoring.application.image.presentation.dto.request;

import fittoring.domain.model.ImageExtension;
import fittoring.domain.model.ImageType;
import jakarta.validation.constraints.NotNull;

public record IssuedPresignedRequest(
        @NotNull(message = "이미지 타입은 필수 입력값입니다.")
        ImageType imageType,
        @NotNull(message = "확장자는 필수 입력값입니다.")
        ImageExtension extension
) {
}
