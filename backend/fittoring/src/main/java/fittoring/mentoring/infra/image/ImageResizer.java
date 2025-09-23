package fittoring.mentoring.infra.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageResizer {

    /**
     * Resize an image MultipartFile to not exceed the specified maximum width.
     *
     * If the input image's width is already less than or equal to maxWidth, the
     * original MultipartFile is returned unchanged. Otherwise the image is resized
     * to maxWidth with the height adjusted to preserve aspect ratio, encoded using
     * the file's original extension (or a default `jpg`), and returned as an
     * in-memory MultipartFile.
     *
     * @param inputFile the uploaded image file to resize
     * @param maxWidth the maximum allowed width (pixels); must be > 0
     * @return the original inputFile if no resizing was necessary, or a new
     *     InmemoryMultipartFile containing the resized image
     * @throws IOException if reading or writing the image data fails
     */
    public MultipartFile resize(MultipartFile inputFile, int maxWidth) throws IOException {
        BufferedImage original = ImageIO.read(inputFile.getInputStream());
        if (original == null) {
            // todo: 우아아아아아아악
        }
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();
        if (originalWidth <= maxWidth) {
            return inputFile;
        }
        int newHeight = (int) Math.round(originalHeight * (maxWidth / (double) originalWidth));

        String originalFilename = inputFile.getOriginalFilename();
        String ext = guessExtension(originalFilename);
        String contentType = contentTypeOf(ext);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Thumbnails.of(original)
                .size(maxWidth, newHeight)
                .keepAspectRatio(true)
                .outputFormat(ext)
                .outputQuality(0.9f)
                .toOutputStream(baos);

        byte[] out = baos.toByteArray();
        return new InmemoryMultipartFile(
                inputFile.getName(),
                safeFilename(originalFilename, ext),
                contentType,
                out
        );
    }

    /**
     * Determine the image file extension from a filename, normalizing and validating it.
     *
     * <p>Returns the lowercase extension if it is one of: "jpg", "jpeg", "png", "gif", or "bmp".
     * The value "jpeg" is normalized to "jpg". If the filename is null, has no extension, or
     * the extension is not one of the accepted values, this method returns the default "jpg".</p>
     *
     * @param originalFilename the original filename to inspect; may be null
     * @return a validated, lowercase image extension ("jpg", "png", "gif", or "bmp"), defaulting to "jpg"
     */
    private static String guessExtension(String originalFilename) {
        if (originalFilename != null) {
            int dot = originalFilename.lastIndexOf('.');
            if (dot > -1 && dot < originalFilename.length() - 1) {
                String e = originalFilename.substring(dot + 1).toLowerCase();
                if (e.equals("jpg") || e.equals("jpeg") || e.equals("png") || e.equals("gif") || e.equals("bmp")) {
                    return e.equals("jpeg") ? "jpg" : e;
                }
            }
        }
        return "jpg";
    }

    /**
     * Returns the MIME content type corresponding to an image file extension.
     *
     * Supported mappings:
     * - "png" -> "image/png"
     * - "gif" -> "image/gif"
     * - "bmp" -> "image/bmp"
     * Any other value maps to "image/jpeg".
     *
     * @param extension lowercase file extension without a leading dot
     * @return the MIME type for the provided extension
     */
    private static String contentTypeOf(String extension) {
        return switch (extension) {
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            default -> "image/jpeg";
        };
    }

    /**
     * Return a safe filename for stored image files.
     *
     * If the provided original filename is null or does not contain an extension (no '.'),
     * a default name of "image.{ext}" is returned; otherwise the originalFilename is returned unchanged.
     *
     * @param originalFilename the original filename from the uploaded file, may be null
     * @param ext the file extension to use when generating a default name (e.g. "jpg", without a leading dot)
     * @return a filename that is safe to use (either the originalFilename or "image.{ext}")
     */
    private static String safeFilename(String originalFilename, String ext) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "image." + ext;
        }
        return originalFilename;
    }
}
