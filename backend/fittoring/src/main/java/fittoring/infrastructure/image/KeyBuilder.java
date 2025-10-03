package fittoring.infrastructure.image;

import fittoring.domain.model.ImageVariant;
import fittoring.infrastructure.InfraErrorMessage;
import fittoring.infrastructure.S3UploadException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class KeyBuilder {

    private static final String KEY_PREFIX = "fit-toring/";

    private KeyBuilder() {
    }

    public static String buildKey(String imageType, ImageVariant variant, String baseName, String extensionWithDot) {
        Objects.requireNonNull(imageType, "imageType은 null이 될 수 없습니다.");
        Objects.requireNonNull(variant, "variant는 null이 될 수 없습니다.");
        Objects.requireNonNull(baseName, "baseName은 null이 될 수 없습니다.");
        Objects.requireNonNull(extensionWithDot, "extensionWithDot은 null이 될 수 없습니다.");

        String variantName = variant.getName();
        return KEY_PREFIX + imageType + "/" + variantName + "/" + baseName + extensionWithDot;
    }

    public static String extractFromUrl(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            return path.startsWith("/") ? path.substring(1) : path;
        } catch (URISyntaxException e) {
            throw new S3UploadException(InfraErrorMessage.S3_UPLOAD_ERROR.getMessage() + url);
        }
    }
}
