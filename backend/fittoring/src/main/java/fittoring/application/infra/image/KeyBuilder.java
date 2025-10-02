package fittoring.application.infra.image;

import fittoring.domain.model.ImageVariant;
import java.util.Objects;

public class KeyBuilder {

    private static final String KEY_PREFIX = "fit-toring/";

    private KeyBuilder() {
    }

    public static String buildKey(String imageType, ImageVariant variant, String baseName, String extensionWithDot) {
        Objects.requireNonNull(imageType, "imageType은 null이 될 수 없습니다.");
        Objects.requireNonNull(variant, "variant는 null이 될 수 없습니다.");
        Objects.requireNonNull(baseName, "baseName은 null이 될 수 없습니다.");
        Objects.requireNonNull(extensionWithDot, "extensionWithDot은 null이 될 수 없습니다.");

        String variantName = variant.getName();
        return KEY_PREFIX + imageType + "/" + variantName + "/" + baseName + extensionWithDot;
    }
}
