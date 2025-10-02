package fittoring.application.infra.image;

import fittoring.domain.model.ImageVariant;

public record VariantUploadResult(
        ImageVariant variant,
        String originalUrl
) {
}
