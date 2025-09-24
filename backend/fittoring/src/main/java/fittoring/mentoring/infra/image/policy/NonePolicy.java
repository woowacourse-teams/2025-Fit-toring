package fittoring.mentoring.infra.image.policy;

import fittoring.mentoring.business.model.ImageVariant;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
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
