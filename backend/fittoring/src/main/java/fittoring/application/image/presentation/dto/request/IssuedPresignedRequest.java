package fittoring.application.image.presentation.dto.request;

import fittoring.domain.model.ImageExtension;
import fittoring.domain.model.ImageType;
import jakarta.validation.constraints.NotNull;

public record IssuedPresignedRequest(
        @NotNull ImageType imageType,
        @NotNull ImageExtension extension
) {
}
