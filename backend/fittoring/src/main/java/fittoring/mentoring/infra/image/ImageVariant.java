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

    public String getName() {
        return this.name().toLowerCase();
    }
}
