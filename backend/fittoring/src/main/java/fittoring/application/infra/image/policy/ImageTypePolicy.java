package fittoring.application.infra.image.policy;

import fittoring.domain.model.ImageVariant;
import java.util.List;

public interface ImageTypePolicy {

    List<ImageVariant> variants();

    int maxWidth(ImageVariant v);
}
