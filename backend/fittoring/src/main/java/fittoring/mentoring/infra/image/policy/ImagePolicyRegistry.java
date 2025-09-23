package fittoring.mentoring.infra.image.policy;

import fittoring.mentoring.business.model.ImageType;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ImagePolicyRegistry {

    private final Map<ImageType, ImageTypePolicy> policyRegistry = Map.of(
            ImageType.MENTORING_PROFILE, new MentoringProfilePolicy(),
            ImageType.CERTIFICATE, new CertificatePolicy()
    );

    /**
     * Retrieves the policy associated with the given image type.
     *
     * @param type the ImageType whose policy is requested
     * @return the corresponding ImageTypePolicy, or {@code null} if no policy is registered for the given type
     */
    public ImageTypePolicy get(ImageType type) {
        return policyRegistry.get(type);
    }
}
