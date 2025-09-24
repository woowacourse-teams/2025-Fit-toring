package fittoring.mentoring.infra.image.policy;

import fittoring.mentoring.infra.image.ImageConstants;
import fittoring.mentoring.business.model.ImageVariant;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MentoringProfilePolicy implements ImageTypePolicy {

    @Override
    public List<ImageVariant> variants() {
        return ImageVariant.getThumbnailRequiredTypes();
    }

    @Override
    public int maxWidth(ImageVariant variant) {
        return (variant == ImageVariant.DEFAULT)
                ? ImageConstants.DEFAULT_MAX_WIDTH
                : ImageConstants.THUMBNAIL_MAX_WIDTH;
    }
}
