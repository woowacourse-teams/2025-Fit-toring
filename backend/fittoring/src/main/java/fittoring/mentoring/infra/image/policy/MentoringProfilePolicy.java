package fittoring.mentoring.infra.image.policy;

import fittoring.mentoring.infra.image.ImageConstants;
import fittoring.mentoring.infra.image.ImageVariant;
import java.util.List;

public class MentoringProfilePolicy implements ImageTypePolicy {

    /**
     * Returns the image variants that require thumbnail generation.
     *
     * @return a list of ImageVariant values for which thumbnails are required
     */
    @Override
    public List<ImageVariant> variants() {
        return ImageVariant.getThumbnailRequiredTypes();
    }

    /**
     * Returns the maximum allowed width (in pixels) for the given image variant.
     *
     * For ImageVariant.DEFAULT this is ImageConstants.DEFAULT_MAX_WIDTH; for all
     * other variants this is ImageConstants.THUMBNAIL_MAX_WIDTH.
     *
     * @param variant the image variant to query
     * @return the maximum width in pixels for the specified variant
     */
    @Override
    public int maxWidth(ImageVariant variant) {
        return (variant == ImageVariant.DEFAULT)
                ? ImageConstants.DEFAULT_MAX_WIDTH
                : ImageConstants.THUMBNAIL_MAX_WIDTH;
    }
}
