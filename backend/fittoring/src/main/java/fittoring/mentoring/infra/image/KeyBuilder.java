package fittoring.mentoring.infra.image;

import fittoring.mentoring.business.model.ImageVariant;

public class KeyBuilder {

    private KeyBuilder() {
    }

    public static String buildKey(String imageType, ImageVariant variant, String baseName, String extensionWithDot) {
        String variantName = variant.getName();
        return "fit-toring/" + imageType + "/" + variantName + "/" + baseName + extensionWithDot;
    }
}
