package fittoring.application.presentation.dto;

import fittoring.application.business.model.ImageExtension;
import fittoring.application.business.model.ImageType;
import jakarta.validation.constraints.NotNull;

public record IssuedPresignedRequest(
        @NotNull ImageType imageType,
        @NotNull ImageExtension extension
) {
}
