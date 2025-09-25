package fittoring.mentoring.infra.image;

import fittoring.mentoring.infra.exception.InfraErrorMessage;
import fittoring.mentoring.infra.exception.S3UploadException;
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

    public Encoded toOriginal(MultipartFile input, String extension, @Nullable String contentType) throws IOException {
        String normalizedExt = normalizeExtension(extension);
        String ct = (contentType == null || contentType.isBlank()) ? contentTypeOf(normalizedExt) : contentType;

        BufferedImage src = ImageIO.read(input.getInputStream());
        if (src == null) {
            throw new S3UploadException(InfraErrorMessage.IMAGE_TRANSCODE_ERROR.getMessage());
        }
        if (normalizedExt.equals("jpg") || normalizedExt.equals("jpeg")) {
            src = flattenIfHasAlpha(src, Color.WHITE);
            normalizedExt = "jpg";
            ct = "image/jpeg";
        }
        byte[] out = writeWithImageIO(src, normalizedExt);
        return new Encoded(out, normalizedExt, ct);
    }

    public Encoded toAvif(MultipartFile input) throws IOException {
        BufferedImage src = ImageIO.read(input.getInputStream());
        if (src == null) {
            throw new S3UploadException(InfraErrorMessage.IMAGE_TRANSCODE_ERROR.getMessage());
        }
        boolean hasAvifWriter = ImageIO.getImageWritersByFormatName("avif").hasNext();
        if (!hasAvifWriter) {
            throw new S3UploadException(InfraErrorMessage.IMAGE_TRANSCODE_ERROR.getMessage());
        }
        byte[] out = writeWithImageIO(src, "avif");
        return new Encoded(out, "avif", "image/avif");
    }

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

    private static byte[] writeWithImageIO(BufferedImage image, String formatName) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            boolean ok = ImageIO.write(image, formatName, baos);
            if (!ok) {
                throw new S3UploadException(InfraErrorMessage.IMAGE_TRANSCODE_ERROR.getMessage());
            }
            return baos.toByteArray();
        }
    }
}
