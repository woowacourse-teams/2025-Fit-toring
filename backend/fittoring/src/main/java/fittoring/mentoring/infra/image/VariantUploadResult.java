package fittoring.mentoring.infra.image;

public record VariantUploadResult(
        ImageVariant variant,
        String originalUrl,
        String avifUrl
) {
}
