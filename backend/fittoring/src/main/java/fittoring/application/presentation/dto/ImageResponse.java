package fittoring.application.presentation.dto;

import fittoring.application.business.model.Image;
import fittoring.application.business.model.ImageType;
import fittoring.application.business.model.ImageVariant;

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
