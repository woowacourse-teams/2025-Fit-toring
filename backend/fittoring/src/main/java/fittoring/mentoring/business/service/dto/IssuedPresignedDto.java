package fittoring.mentoring.business.service.dto;

import fittoring.mentoring.business.model.ImageExtension;
import fittoring.mentoring.business.model.ImageType;

public record IssuedPresignedDto(
        ImageType imageType,
        ImageExtension extension
) {
}
