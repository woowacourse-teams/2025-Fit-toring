package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.ImageVariant;
import fittoring.mentoring.business.repository.ImageRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public Optional<Image> save(ImageType type, Long relationId, String imageUrl) {
        return Optional.of(imageRepository.save(new Image(
                imageUrl,
                type,
                relationId
        )));
    }

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
