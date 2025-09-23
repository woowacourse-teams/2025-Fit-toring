package fittoring.mentoring.infra.image;

import java.util.List;

public enum ImageVariant {

    DEFAULT,
    THUMBNAIL,
    ;

    public static List<ImageVariant> getThumbnailRequiredTypes() {
        return List.of(DEFAULT, THUMBNAIL);
    }

    public static List<ImageVariant> getDefaultType() {
        return List.of(DEFAULT);
    }

    public static String getName(ImageVariant variant) {
        for (ImageVariant value : values()) {
            if (value == variant) {
                return variant.name().toLowerCase();
            }
        }
        return "default";
    }
}
