package fittoring.infrastructure.image;

import fittoring.domain.model.ImageVariant;
import fittoring.infrastructure.exception.InfraErrorMessage;
import fittoring.infrastructure.exception.S3UploadException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyBuilder {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.project-prefix}")
    private String projectKeyPrefix;

    @Value("${aws.s3.env-prefix}")
    private String envKeyPrefix;

    public String buildKey(String imageType, ImageVariant variant, String baseName, String extensionWithDot) {
        Objects.requireNonNull(imageType, "imageType은 null이 될 수 없습니다.");
        Objects.requireNonNull(variant, "variant는 null이 될 수 없습니다.");
        Objects.requireNonNull(baseName, "baseName은 null이 될 수 없습니다.");
        Objects.requireNonNull(extensionWithDot, "extensionWithDot은 null이 될 수 없습니다.");

        String variantName = variant.getName();
        return projectKeyPrefix + envKeyPrefix + imageType + "/" + variantName + "/" + baseName + extensionWithDot;
    }

    public String extractKeyFromUrl(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();

            if (path == null || path.isBlank()) {
                throw new S3UploadException(InfraErrorMessage.S3_UPLOAD_ERROR.getMessage() + url);
            }
            String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);

            if (decodedPath.startsWith("/" + bucketName + "/")) {
                decodedPath = decodedPath.substring(bucketName.length() + 2);
            }
            if (decodedPath.startsWith("/")) {
                decodedPath = decodedPath.substring(1);
            }
            String expectedPrefix = projectKeyPrefix + envKeyPrefix;
            if (!decodedPath.startsWith(expectedPrefix)) {
                throw new S3UploadException("S3 key prefix 불일치: " + decodedPath);
            }
            return decodedPath;
        } catch (URISyntaxException e) {
            throw new S3UploadException(InfraErrorMessage.S3_UPLOAD_ERROR.getMessage() + url);
        }
    }

    public String extractBaseNameFromUrl(String url) {
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
