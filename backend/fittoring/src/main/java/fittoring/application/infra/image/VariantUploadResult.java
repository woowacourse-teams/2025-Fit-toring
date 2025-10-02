package fittoring.application.infra.image;

import fittoring.application.business.model.ImageVariant;

public record VariantUploadResult(
        ImageVariant variant,
        String originalUrl
) {
}
