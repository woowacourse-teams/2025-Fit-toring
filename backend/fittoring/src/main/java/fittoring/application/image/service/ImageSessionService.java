package fittoring.application.image.service;

import fittoring.application.image.repository.ImageRepository;
import fittoring.application.image.repository.ImageSessionRepository;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.ImageVariant;
import fittoring.infrastructure.dto.ImageReadyMessageDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ImageSessionService {

    private final ImageRepository imageRepository;
    private final ImageSessionRepository imageSessionRepository;

    @Transactional
    public void imageProcessor(ImageReadyMessageDto msg) {
        ImageType imageType = ImageType.fromDir(msg.imageType());
        ImageVariant imageVariant = ImageVariant.from(msg.imageVariant());

        if (imageRepository.existsByBaseNameAndImageVariant(msg.baseName(), imageVariant)) {
            return;
        }

        Optional<Image> original = imageRepository.findByImageTypeAndBaseNameAndImageVariant(
                imageType,
                msg.baseName(),
                ImageVariant.DEFAULT
        );

        if (original.isPresent()) {
            Long relationId = original.get().getRelationId();
            imageRepository.upsert(
                    msg.url(),
                    imageType.name(),
                    imageVariant.name(),
                    relationId,
                    msg.baseName()
            );
            imageSessionRepository.deleteByBaseNameAndImageVariant(msg.baseName(), imageVariant);
            return;
        }
        imageSessionRepository.upsert(
                msg.baseName(),
                imageType.name(),
                imageVariant.name(),
                msg.url()
        );
    }
}
