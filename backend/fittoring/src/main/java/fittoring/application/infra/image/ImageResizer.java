package fittoring.application.infra.image;

import fittoring.application.infra.exception.InfraErrorMessage;
import fittoring.application.infra.exception.S3UploadException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageResizer {

    public MultipartFile resize(MultipartFile inputFile, int maxWidth) throws IOException {
        BufferedImage original = ImageIO.read(inputFile.getInputStream());
        if (original == null) {
            throw new S3UploadException(InfraErrorMessage.IMAGE_RESIZING_ERROR.getMessage());
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

    private static String contentTypeOf(String extension) {
        return switch (extension) {
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            default -> "image/jpeg";
        };
    }

    private static String safeFilename(String originalFilename, String ext) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "image." + ext;
        }
        return originalFilename;
    }
}
