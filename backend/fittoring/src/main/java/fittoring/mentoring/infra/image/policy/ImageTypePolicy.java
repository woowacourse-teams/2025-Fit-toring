package fittoring.mentoring.infra.image.policy;

import fittoring.mentoring.infra.image.ImageVariant;
import java.util.List;

public interface ImageTypePolicy {

    List<ImageVariant> variants();

    int maxWidth(ImageVariant v);
}
