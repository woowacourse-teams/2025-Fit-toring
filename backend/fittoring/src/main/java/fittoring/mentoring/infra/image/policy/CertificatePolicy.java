package fittoring.mentoring.infra.image.policy;

import fittoring.mentoring.infra.image.ImageConstants;
import fittoring.mentoring.business.model.ImageVariant;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
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
