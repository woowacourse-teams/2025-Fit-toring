package fittoring.application.presentation.dto;

import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.ImageVariant;

public record ImageResponse(
        String url,
        ImageType imageType,
        ImageVariant imageVariant,
        Long relationId
) {

    public static ImageResponse from(Image image) {
        return new ImageResponse(
                image.getUrl(),
                image.getImageType(),
                image.getImageVariant(),
                image.getRelationId()
        );
    }
}
