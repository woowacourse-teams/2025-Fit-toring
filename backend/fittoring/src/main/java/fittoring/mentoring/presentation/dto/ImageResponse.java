package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.ImageVariant;

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
