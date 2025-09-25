package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.ImageType;

public record ImageRequest(
        ImageType imageType,
        Long relationId
) {
}
