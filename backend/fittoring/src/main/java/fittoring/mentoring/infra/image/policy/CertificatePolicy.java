package fittoring.mentoring.infra.image.policy;

import fittoring.mentoring.infra.image.ImageConstants;
import fittoring.mentoring.infra.image.ImageVariant;
import java.util.List;

public class CertificatePolicy implements ImageTypePolicy {

    @Override
    public List<ImageVariant> variants() {
        return ImageVariant.getDefaultType();
    }

    @Override
    public int maxWidth(ImageVariant v) {
        return ImageConstants.DEFAULT_MAX_WIDTH;
    }
}
