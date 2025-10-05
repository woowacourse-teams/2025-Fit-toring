package fittoring.infrastructure.image;

import fittoring.domain.model.ImageVariant;
import fittoring.infrastructure.exception.InfraErrorMessage;
import fittoring.infrastructure.exception.S3UploadException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class KeyBuilder {

    private static final String KEY_PREFIX = "fit-toring/";

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

            if (path == null || path.isBlank()) {
                throw new S3UploadException(InfraErrorMessage.S3_UPLOAD_ERROR.getMessage() + url);
            }

            String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);

            return decodedPath.startsWith("/") ? decodedPath.substring(1) : decodedPath;
        } catch (URISyntaxException e) {
            throw new S3UploadException(InfraErrorMessage.S3_UPLOAD_ERROR.getMessage() + url);
        }
    }

    public static String extractBaseNameFromUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        try {
            String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8);

            int queryIndex = decodedUrl.indexOf('?');
            if (queryIndex != -1) {
                decodedUrl = decodedUrl.substring(0, queryIndex);
            }

            int lastSlash = decodedUrl.lastIndexOf('/');
            String filename = (lastSlash != -1)
                    ? decodedUrl.substring(lastSlash + 1)
                    : decodedUrl;

            int dotIndex = filename.lastIndexOf('.');
            if (dotIndex != -1) {
                return filename.substring(0, dotIndex);
            }
            return filename;
        } catch (Exception e) {
            return null;
        }
    }
}
