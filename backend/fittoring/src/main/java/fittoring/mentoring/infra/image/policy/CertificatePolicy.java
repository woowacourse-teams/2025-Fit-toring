package fittoring.mentoring.infra.image.policy;

import fittoring.mentoring.infra.image.ImageConstants;
import fittoring.mentoring.infra.image.ImageVariant;
import java.util.List;

public class CertificatePolicy implements ImageTypePolicy {

    /**
     * Returns the default set of image variants for certificates.
     *
     * @return a list of default {@link ImageVariant} values for this image type
     */
    @Override
    public List<ImageVariant> variants() {
        return ImageVariant.getDefaultType();
    }

    /**
     * Returns the maximum allowed width for certificate images.
     *
     * This implementation ignores the provided image variant and always returns
     * the default maximum width.
     *
     * @param v the image variant (ignored)
     * @return the maximum width in pixels (ImageConstants.DEFAULT_MAX_WIDTH)
     */
    @Override
    public int maxWidth(ImageVariant v) {
        return ImageConstants.DEFAULT_MAX_WIDTH;
    }
}
