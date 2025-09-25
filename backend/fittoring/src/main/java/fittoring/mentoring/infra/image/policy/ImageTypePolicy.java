package fittoring.mentoring.infra.image.policy;

import fittoring.mentoring.business.model.ImageVariant;
import java.util.List;

public interface ImageTypePolicy {

    List<ImageVariant> variants();

    int maxWidth(ImageVariant v);
}
