package fittoring.application.image.service;

import fittoring.application.image.repository.ImageRepository;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.ImageVariant;
import fittoring.infrastructure.image.KeyBuilder;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Transactional
    public Image save(ImageType type, Long relationId, String imageUrl) {
        String baseName = KeyBuilder.extractBaseNameFromUrl(imageUrl);
        return imageRepository.save(new Image(
                imageUrl,
                type,
                relationId,
                baseName
        ));
    }

    @Transactional
    public List<Image> saveAll(List<Image> images) {
        return imageRepository.saveAll(images);
    }

    public Optional<Image> findByImageTypeAndRelationId(ImageType imageType, Long relationId) {
        return imageRepository.findByImageTypeAndRelationIdAndImageVariant(imageType, relationId, ImageVariant.DEFAULT);
    }

    public Optional<Image> findThumbnailByImageTypeAndRelationId(ImageType imageType, Long relationId) {
        List<Image> thumbnailImages = imageRepository.findThumbnailByImageTypeAndRelationId(
                relationId,
                imageType,
                ImageVariant.THUMBNAIL,
                ImageVariant.DEFAULT
        );
        if (thumbnailImages.isEmpty()) {
            return Optional.empty();
        }
        return thumbnailImages.stream()
                .filter(img -> img.getImageVariant() == ImageVariant.THUMBNAIL)
                .findFirst()
                .or(() -> thumbnailImages.stream()
                        .filter(img -> img.getImageVariant() == ImageVariant.DEFAULT)
                        .findFirst()
                );
    }

    public List<Image> findByRelationIdsAndImageType(List<Long> certificateIds, ImageType imageType) {
        return imageRepository.findByRelationIdsAndImageType(certificateIds, imageType);
    }

    public void deleteByImageTypeAndRelationId(ImageType imageType, Long relationId) {
        imageRepository.deleteByImageTypeAndRelationId(imageType, relationId);
    }
}
