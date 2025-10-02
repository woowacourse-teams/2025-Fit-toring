package fittoring.application.infra.image.policy;

import fittoring.application.infra.image.ImageConstants;
import fittoring.domain.model.ImageVariant;
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
