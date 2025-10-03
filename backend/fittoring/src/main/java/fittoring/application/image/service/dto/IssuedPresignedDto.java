package fittoring.application.image.service.dto;

import fittoring.domain.model.ImageExtension;
import fittoring.domain.model.ImageType;

public record IssuedPresignedDto(
        ImageType imageType,
        ImageExtension extension
) {
}
