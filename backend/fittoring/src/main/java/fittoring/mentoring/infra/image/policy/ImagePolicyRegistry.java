package fittoring.mentoring.infra.image.policy;

import fittoring.mentoring.business.model.ImageType;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ImagePolicyRegistry {

    private final Map<ImageType, ImageTypePolicy> policyRegistry;

    public ImagePolicyRegistry(
            MentoringProfilePolicy mentoringProfilePolicy,
            CertificatePolicy certificatePolicy,
            NonePolicy nonePolicy
    ) {
        this.policyRegistry = Map.of(
                ImageType.MENTORING_PROFILE, mentoringProfilePolicy,
                ImageType.CERTIFICATE, certificatePolicy,
                ImageType.NONE, nonePolicy
        );
    }

    public ImageTypePolicy get(ImageType type) {
        return policyRegistry.get(type);
    }
}
