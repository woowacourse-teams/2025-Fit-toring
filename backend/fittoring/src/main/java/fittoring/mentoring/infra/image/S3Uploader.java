package fittoring.mentoring.infra.image;

import fittoring.mentoring.business.model.ImageVariant;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * legacy 클래스입니다. 이제 서버에서 이미지를 직접 업로드하지 않고 presigned-url을 발급하는 방식으로 수정됩니다.
 */
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final S3Client s3Client;
    private final ImageResizer imageResizer;
    private final ImageTranscoder imageTranscoder;

    private final String bucketName = "techcourse-project-2025";

    public VariantUploadResult uploadVariant(
            MultipartFile originalInput,
            String imageTypeName,
            ImageVariant variant,
            int maxWidth,
            String baseName
    ) throws IOException {
        MultipartFile resized = imageResizer.resize(originalInput, maxWidth);

        String originalExtension = extensionOf(resized.getOriginalFilename());
        String originalContentType = contentTypeOf(originalExtension);

        Encoded orig = imageTranscoder.toOriginal(resized, originalExtension, originalContentType);
        String keyOriginal = KeyBuilder.buildKey(imageTypeName, variant, baseName, "." + orig.extension());
        putObject(keyOriginal, orig.contentType(), orig.bytes());
        String urlOriginal = getUrl(keyOriginal);

        return new VariantUploadResult(variant, urlOriginal);
    }

    private void putObject(String key, String contentType, byte[] bytes) {
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .cacheControl("public, max-age=31536000, immutable")
                .build();
        s3Client.putObject(req, RequestBody.fromBytes(bytes));
    }

    private static String extensionOf(String name) {
        if (name != null) {
            int dot = name.lastIndexOf('.');
            if (dot > -1 && dot < name.length() - 1) {
                String e = name.substring(dot + 1).toLowerCase();
                if (e.equals("jpeg")) {
                    return "jpg";
                }
                if (e.matches("jpg|png|gif|bmp|webp")) {
                    return e;
                }
            }
        }
        return "jpg";
    }

    private static String contentTypeOf(String extension) {
        return switch (extension) {
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            default -> "image/jpeg";
        };
    }

    private String getUrl(String key) {
        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }
}
