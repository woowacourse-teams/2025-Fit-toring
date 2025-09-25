package fittoring.mentoring.infra.image;

import fittoring.mentoring.business.model.ImageVariant;

public record VariantUploadResult(
        ImageVariant variant,
        String originalUrl
) {
}
