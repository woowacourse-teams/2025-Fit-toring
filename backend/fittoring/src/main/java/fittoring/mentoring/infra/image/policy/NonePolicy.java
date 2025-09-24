package fittoring.mentoring.infra.image.policy;

import fittoring.mentoring.business.model.ImageVariant;
import java.util.List;

public class NonePolicy implements ImageTypePolicy {

    @Override
    public List<ImageVariant> variants() {
        return List.of(ImageVariant.DEFAULT);
    }

    @Override
    public int maxWidth(ImageVariant v) {
        return 500;
    }
}
