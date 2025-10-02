package fittoring.application.presentation.dto;

import fittoring.application.business.model.ImageType;

public record ImageRequest(
        ImageType imageType,
        Long relationId
) {
}
