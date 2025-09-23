package fittoring.mentoring.infra.image;

import jakarta.annotation.Nullable;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageTranscoder {

    /**
     * Transcodes the provided image into the requested "original" image format and returns its encoded bytes, final extension, and content type.
     *
     * The provided extension is normalized (defaults to "jpg" when null/unknown). If contentType is null or blank, the MIME type is derived from the normalized extension.
     * When the target format is JPEG ("jpg" or "jpeg"), any alpha channel is flattened onto a white background and the final extension/content type are set to "jpg"/"image/jpeg".
     *
     * @param input the source image as a MultipartFile
     * @param extension the desired output file extension (may be null or include a leading dot); normalized when determining output format
     * @param contentType optional explicit MIME type to use for the result; if null or blank, the MIME type is derived from the normalized extension
     * @return an Encoded containing the image bytes, the final extension, and the final content type
     * @throws IOException if reading from the input or writing the output image fails
     */
    public Encoded toOriginal(MultipartFile input, String extension, @Nullable String contentType) throws IOException {
        String normalizedExt = normalizeExtension(extension);
        String ct = (contentType == null || contentType.isBlank()) ? contentTypeOf(normalizedExt) : contentType;

        BufferedImage src = ImageIO.read(input.getInputStream());
        if (src == null) {
            // todo: 적절한 예외 반환
        }
        if (normalizedExt.equals("jpg") || normalizedExt.equals("jpeg")) {
            src = flattenIfHasAlpha(src, Color.WHITE);
            normalizedExt = "jpg";
            ct = "image/jpeg";
        }
        byte[] out = writeWithImageIO(src, normalizedExt);
        return new Encoded(out, normalizedExt, ct);
    }

    /**
     * Transcodes the provided image into AVIF format and returns the encoded result.
     *
     * Reads the image from the given MultipartFile, encodes it as AVIF using ImageIO, and
     * returns an Encoded containing the AVIF bytes, the "avif" extension, and the
     * "image/avif" content type.
     *
     * @param input the uploaded image file to transcode
     * @return an Encoded containing AVIF bytes, extension "avif", and content type "image/avif"
     * @throws IOException if an I/O error occurs while reading or writing image data
     */
    public Encoded toAvif(MultipartFile input) throws IOException {
        BufferedImage src = ImageIO.read(input.getInputStream());
        if (src == null) {
            // todo: 적절한 예외 반환
        }
        boolean hasAvifWriter = ImageIO.getImageWritersByFormatName("avif").hasNext();
        if (!hasAvifWriter) {
            // todo: 적절한 예외 반환
        }
        byte[] out = writeWithImageIO(src, "avif");
        return new Encoded(out, "avif", "image/avif");
    }

    /**
     * Normalize a file extension to a supported image extension.
     *
     * <p>Converts null or unknown values to "jpg". Strips a leading dot, lowercases the input,
     * maps "jpeg" to "jpg", and only allows "jpg", "png", "gif", "bmp", and "webp".</p>
     *
     * @param ext the input extension or filename fragment (may be null or start with a dot)
     * @return a normalized extension guaranteed to be one of: "jpg", "png", "gif", "bmp", "webp"
     */
    private static String normalizeExtension(String ext) {
        if (ext == null) {
            return "jpg";
        }
        String e = ext.toLowerCase().replace(".", "");
        if (e.equals("jpeg")) {
            return "jpg";
        }
        return switch (e) {
            case "jpg", "png", "gif", "bmp", "webp" -> e;
            default -> "jpg";
        };
    }

    /**
     * Returns the MIME content type for a given image file extension.
     *
     * The method expects a normalized extension (lowercase, no leading dot; e.g. "jpg", "png", "avif").
     *
     * @param extension the image file extension to map
     * @return the corresponding MIME type (defaults to "image/jpeg" for unknown extensions)
     */
    private static String contentTypeOf(String extension) {
        return switch (extension) {
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            case "avif" -> "image/avif";
            default -> "image/jpeg";
        };
    }

    /**
     * If the source image has an alpha channel, returns a new RGB image with transparency
     * composited over the provided background color; otherwise returns the original image.
     *
     * @param src the source BufferedImage to inspect and (if needed) flatten
     * @param bg  the background Color to composite beneath transparent pixels when flattening
     * @return an RGB BufferedImage with no alpha (a new image if flattening was required, or the original {@code src} if not)
     */
    private static BufferedImage flattenIfHasAlpha(BufferedImage src, Color bg) {
        boolean hasAlpha = src.getColorModel().hasAlpha();
        if (!hasAlpha) {
            return src;
        }

        BufferedImage rgb = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D rgbGraphicImage = rgb.createGraphics();
        try {
            rgbGraphicImage.setComposite(AlphaComposite.SrcOver);
            rgbGraphicImage.setColor(bg);
            rgbGraphicImage.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
            rgbGraphicImage.drawImage(src, 0, 0, null);
        } finally {
            rgbGraphicImage.dispose();
        }
        return rgb;
    }

    /**
     * Encodes a BufferedImage into a byte array using ImageIO with the given format name.
     *
     * Attempts to write the provided image to an in-memory stream in the specified
     * format and returns the resulting bytes.
     *
     * @param image      the image to encode
     * @param formatName the target image format (e.g., "jpg", "png", "avif")
     * @return the encoded image as a byte array
     * @throws IOException if an I/O error occurs while writing or if no ImageIO writer
     *                     is available for the specified format
     */
    private static byte[] writeWithImageIO(BufferedImage image, String formatName) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            boolean ok = ImageIO.write(image, formatName, baos);
            if (!ok) {
                // todo: 적절한 예외 반환
            }
            return baos.toByteArray();
        }
    }
}
