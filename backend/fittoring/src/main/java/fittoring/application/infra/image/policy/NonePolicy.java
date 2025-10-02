package fittoring.application.infra.image.policy;

import fittoring.domain.model.ImageVariant;
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
