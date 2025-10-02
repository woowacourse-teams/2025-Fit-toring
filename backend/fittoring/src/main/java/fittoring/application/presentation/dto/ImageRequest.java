package fittoring.application.presentation.dto;

import fittoring.domain.model.ImageType;

public record ImageRequest(
        ImageType imageType,
        Long relationId
) {
}
