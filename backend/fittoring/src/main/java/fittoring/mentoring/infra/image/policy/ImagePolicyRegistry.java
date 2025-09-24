package fittoring.mentoring.infra.image.policy;

import fittoring.mentoring.business.model.ImageType;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ImagePolicyRegistry {

    private final Map<ImageType, ImageTypePolicy> policyRegistry = Map.of(
            ImageType.MENTORING_PROFILE, new MentoringProfilePolicy(),
            ImageType.CERTIFICATE, new CertificatePolicy(),
            ImageType.NONE, new NonePolicy()
    );

    public ImageTypePolicy get(ImageType type) {
        return policyRegistry.get(type);
    }
}
