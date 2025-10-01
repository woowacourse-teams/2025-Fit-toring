package fittoring.mentoring.infra.image;

import fittoring.mentoring.business.model.ImageVariant;

// TODO: api 작업 후 deprecated
public record VariantUploadResult(
        ImageVariant variant,
        String originalUrl
) {
}
