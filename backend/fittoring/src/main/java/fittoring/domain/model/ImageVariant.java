package fittoring.domain.model;

public enum ImageVariant {

    DEFAULT("default"),
    THUMBNAIL("thumbnail"),
    ;

    private final String alias;

    ImageVariant(String alias) {
        this.alias = alias;
    }

    public static ImageVariant from(String imageVariant) {
        for (ImageVariant value : values()) {
            if (value.alias.equalsIgnoreCase(imageVariant)) {
                return value;
            }
        }
        return DEFAULT;
    }

    public String getName() {
        return this.name().toLowerCase();
    }
}
