package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.ImageExtension;
import fittoring.mentoring.business.model.ImageType;
import jakarta.validation.constraints.NotNull;

public record IssuedPresignedRequest(
        @NotNull ImageType imageType,
        @NotNull ImageExtension extension
) {
}
