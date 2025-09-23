package fittoring.mentoring.infra.image.policy;

import fittoring.mentoring.infra.image.ImageVariant;
import java.util.List;

public interface ImageTypePolicy {

    /**
 * Returns the image variants supported by this policy.
 *
 * @return a list of ImageVariant values representing the variants applicable for this image type
 */
List<ImageVariant> variants();

    /**
 * Returns the maximum allowed width for the given image variant.
 *
 * @param v the image variant to query
 * @return the maximum width for the variant in pixels
 */
int maxWidth(ImageVariant v);
}
