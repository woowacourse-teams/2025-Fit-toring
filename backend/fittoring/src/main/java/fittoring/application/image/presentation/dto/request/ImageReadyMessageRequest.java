package fittoring.application.image.presentation.dto.request;

import fittoring.domain.model.ImageType;
import fittoring.domain.model.ImageVariant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ImageReadyMessageRequest(
        @NotBlank
        String event,
        @NotNull
        ImageType imageType,
        @NotBlank
        String baseName,
        @NotNull
        ImageVariant imageVariant,
        @NotBlank
        String url
) {
}
