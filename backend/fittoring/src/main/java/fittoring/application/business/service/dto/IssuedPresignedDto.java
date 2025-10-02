package fittoring.application.business.service.dto;

import fittoring.application.business.model.ImageExtension;
import fittoring.application.business.model.ImageType;

public record IssuedPresignedDto(
        ImageType imageType,
        ImageExtension extension
) {
}
