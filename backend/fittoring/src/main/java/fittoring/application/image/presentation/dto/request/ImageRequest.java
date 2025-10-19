package fittoring.application.image.presentation.dto.request;

import fittoring.domain.model.ImageType;

public record ImageRequest(
        ImageType imageType,
        Long relationId
) {
}
