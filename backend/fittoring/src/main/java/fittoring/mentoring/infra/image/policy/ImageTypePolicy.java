package fittoring.mentoring.infra.image.policy;

import fittoring.mentoring.business.model.ImageVariant;
import java.util.List;

// TODO: api 작업 후 deprecated
public interface ImageTypePolicy {

    List<ImageVariant> variants();

    int maxWidth(ImageVariant v);
}
