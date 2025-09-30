package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.ImageExtension;
import fittoring.mentoring.business.model.ImageType;
import jakarta.validation.constraints.NotBlank;

public record IssuedPresignedRequest(
        @NotBlank ImageType imageType,
        @NotBlank ImageExtension extension
) {
}
