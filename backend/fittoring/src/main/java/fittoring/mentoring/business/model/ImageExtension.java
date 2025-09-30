package fittoring.mentoring.business.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.UnsupportedImageExtensionException;
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

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ImageExtension from(String value) {
        return Arrays.stream(values())
                .filter(ext -> ext.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->
                        new UnsupportedImageExtensionException(
                                BusinessErrorMessage.UNSUPPORTED_IMAGE_EXTENSION.getMessage()
                        )
                );
    }
}
