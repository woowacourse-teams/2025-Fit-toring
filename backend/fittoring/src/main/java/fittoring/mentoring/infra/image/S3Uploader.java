package fittoring.mentoring.infra.image;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final S3Client s3Client;
    private final ImageResizer imageResizer;
    private final ImageTranscoder imageTranscoder;

    private final String bucketName = "techcourse-project-2025";

    /**
     * Resizes the given image to the specified max width, transcodes and uploads two variants
     * (original-format and AVIF) to S3, and returns their public URLs.
     *
     * The method:
     * - resizes the provided MultipartFile to maxWidth,
     * - determines the normalized extension and MIME type for the resized image,
     * - produces an encoded original-format payload and an AVIF payload via the ImageTranscoder,
     * - uploads both payloads to S3 under keys derived from imageTypeName, variant and baseName,
     * - returns a VariantUploadResult containing the variant and the public URLs for both uploads.
     *
     * @param originalInput the uploaded image to process
     * @param imageTypeName a logical image type used when building the S3 key (e.g., "profile", "cover")
     * @param variant       the ImageVariant describing size/fit category for this upload
     * @param maxWidth      maximum width (in pixels) to resize the image to before transcoding
     * @param baseName      base filename (without extension) to use when building the S3 object key
     * @return a VariantUploadResult containing the variant and public URLs for the original-format and AVIF uploads
     * @throws IOException if an I/O error occurs during resizing, transcoding, or upload
     */
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
        String keyOriginal = buildKey(imageTypeName, variant, baseName, "." + orig.extension());
        putObject(keyOriginal, orig.contentType(), orig.bytes());
        String urlOriginal = getUrl(keyOriginal);

        Encoded avif = imageTranscoder.toAvif(resized);
        String keyAvif = buildKey(imageTypeName, variant, baseName, "." + avif.extension());
        putObject(keyAvif, avif.contentType(), avif.bytes());
        String urlAvif = getUrl(keyAvif);

        return new VariantUploadResult(variant, urlOriginal, urlAvif);
    }

    /**
     * Uploads the given bytes to the configured S3 bucket under the specified key with the provided Content-Type
     * and a long-lived public Cache-Control header ("public, max-age=31536000, immutable").
     *
     * @param key         the S3 object key (path within the bucket)
     * @param contentType the MIME type to set on the uploaded object (e.g., "image/jpeg")
     * @param bytes       the raw bytes to upload as the object's body
     */
    private void putObject(String key, String contentType, byte[] bytes) {
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .cacheControl("public, max-age=31536000, immutable")
                .build();
        s3Client.putObject(req, RequestBody.fromBytes(bytes));
    }

    /**
     * Build the S3 object key path for an image variant.
     *
     * The key has the form "fit-toring/{imageType}/{variantName}/{baseName}{extensionWithDot}".
     *
     * @param imageType the image type/category used as the top-level folder (e.g., "profile")
     * @param variant the image variant enum value; its name is resolved with ImageVariant.getName(...)
     * @param baseName the filename without extension
     * @param extensionWithDot the file extension including the leading dot (e.g., ".jpg")
     * @return the complete S3 key for storing the image variant
     */
    private static String buildKey(String imageType, ImageVariant variant, String baseName, String extensionWithDot) {
        String variantName = ImageVariant.getName(variant);
        return "fit-toring/" + imageType + "/" + variantName + "/" + baseName + extensionWithDot;
    }

    /**
     * Derives a normalized image file extension (without leading dot) from a file name.
     *
     * <p>Accepts a file name or path and returns the lowercase extension. Maps "jpeg" to "jpg",
     * accepts "jpg", "png", "gif", "bmp", and "webp" as-is, and falls back to "jpg" when the
     * input is null, has no extension, or the extension is unrecognized.</p>
     *
     * @param name file name or path from which to extract the extension; may be null
     * @return normalized extension (one of "jpg", "png", "gif", "bmp", "webp"); defaults to "jpg"
     */
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

    /**
     * Returns the MIME content type for a given image file extension.
     *
     * <p>Maps common extensions ("png", "gif", "bmp", "webp") to their corresponding
     * image MIME types and returns "image/jpeg" for any other value.
     *
     * @param extension the image file extension (expected without a leading dot, typically lowercase)
     * @return the corresponding MIME type (e.g., "image/png", "image/jpeg")
     */
    private static String contentTypeOf(String extension) {
        return switch (extension) {
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            default -> "image/jpeg";
        };
    }

    /**
     * Builds the public HTTPS URL for an object stored in the configured S3 bucket.
     *
     * @param key the S3 object key (path within the bucket)
     * @return the full public URL (https) to access the object
     */
    private String getUrl(String key) {
        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }
}
