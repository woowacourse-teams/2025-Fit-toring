package fittoring.mentoring.infra.image;

import java.util.List;

public enum ImageVariant {

    DEFAULT,
    THUMBNAIL,
    ;

    /**
     * Returns the image variants that must be generated when creating thumbnails.
     *
     * @return an unmodifiable list of ImageVariant required for thumbnail creation: DEFAULT and THUMBNAIL
     */
    public static List<ImageVariant> getThumbnailRequiredTypes() {
        return List.of(DEFAULT, THUMBNAIL);
    }

    /**
     * Returns the list of image variants considered the default type.
     *
     * @return an immutable list containing the {@link ImageVariant#DEFAULT} variant
     */
    public static List<ImageVariant> getDefaultType() {
        return List.of(DEFAULT);
    }

    /**
     * Returns the canonical lowercase name for the given image variant.
     *
     * If the provided variant is null or not one of the enum values, this returns "default".
     *
     * @param variant the ImageVariant to convert to its lowercase name
     * @return the lowercase name of the variant, or "default" when the variant is null/unrecognized
     */
    public static String getName(ImageVariant variant) {
        for (ImageVariant value : values()) {
            if (value == variant) {
                return variant.name().toLowerCase();
            }
        }
        return "default";
    }
}
