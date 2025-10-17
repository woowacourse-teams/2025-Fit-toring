package fittoring.application.image.service;

import fittoring.application.image.repository.ImageRepository;
import fittoring.application.image.repository.ImageSessionRepository;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageSession;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.ImageVariant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class ImageSessionJob {

    private final ImageSessionRepository imageSessionRepository;
    private final ImageRepository imageRepository;

    @Value("${image-session.merge.enabled}")
    private boolean enabled;

    @Value("${image-session.merge.batch-size}")
    private int batchSize;

    @Transactional
    @Scheduled(fixedDelayString = "${image-session.merge.fixed-delay-ms}")
    public void mergeLeftovers() {
        if (!enabled) {
            return;
        }
        List<ImageSession> batch = imageSessionRepository.pickBatchForMerge(batchSize);
        if (batch.isEmpty()) {
            return;
        }
        for (ImageSession imageSession : batch) {
            convertImageSessionToImage(imageSession);
        }
    }

    private void convertImageSessionToImage(ImageSession imageSession) {
        ImageType type = imageSession.getImageType();
        ImageVariant variant = imageSession.getImageVariant();

        if (imageRepository.existsByBaseNameAndImageVariant(imageSession.getBaseName(), variant)) {
            imageSessionRepository.deleteByBaseNameAndImageVariant(imageSession.getBaseName(), variant);
            return;
        }

        Optional<Image> original = imageRepository.findByImageTypeAndBaseNameAndImageVariant(
                type, imageSession.getBaseName(), ImageVariant.DEFAULT);

        if (original.isPresent()) {
            Long relationId = original.get().getRelationId();
            imageRepository.upsert(
                    imageSession.getUrl(),
                    type.name(),
                    variant.name(),
                    relationId,
                    imageSession.getBaseName()
            );
            imageSessionRepository.deleteByBaseNameAndImageVariant(imageSession.getBaseName(), variant);
        }
    }
}
