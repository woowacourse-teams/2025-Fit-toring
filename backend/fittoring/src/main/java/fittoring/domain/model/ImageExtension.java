package fittoring.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.UnsupportedImageExtensionException;
import java.util.Arrays;

public enum ImageExtension {

    JPG("jpg"),
    JPEG("jpeg"),
    PNG("png"),
    WEBP("webp"),
    AVIF("avif"),
    ;

    private final String value;

    ImageExtension(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ImageExtension from(String value) {
        return Arrays.stream(values())
                .filter(ext -> ext.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new UnsupportedImageExtensionException(
                        BusinessErrorMessage.UNSUPPORTED_IMAGE_EXTENSION.getMessage())
                );
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
