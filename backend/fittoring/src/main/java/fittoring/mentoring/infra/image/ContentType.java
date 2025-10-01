package fittoring.mentoring.infra.image;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.UnsupportedImageExtensionException;
import fittoring.mentoring.business.model.ImageExtension;
import java.util.Locale;

public class ContentType {

    public static String of(String extension) {
        return switch (extension.toLowerCase(Locale.ROOT)) {
            case "png" -> "image/png";
            case "jpeg", "jpg" -> "image/jpeg";
            case "webp" -> "image/webp";
            case "avif" -> "image/avif";
            default -> throw new UnsupportedImageExtensionException(
                    BusinessErrorMessage.UNSUPPORTED_IMAGE_EXTENSION.getMessage());
        };
    }

    public static String of(ImageExtension extension) {
        return switch (extension) {
            case PNG -> "image/png";
            case JPEG, JPG -> "image/jpeg";
            case WEBP -> "image/webp";
            case AVIF -> "image/avif";
            default -> throw new UnsupportedImageExtensionException(
                    BusinessErrorMessage.UNSUPPORTED_IMAGE_EXTENSION.getMessage());
        };
    }
}
