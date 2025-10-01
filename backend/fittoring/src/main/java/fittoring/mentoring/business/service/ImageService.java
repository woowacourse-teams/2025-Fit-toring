package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.ImageVariant;
import fittoring.mentoring.business.repository.ImageRepository;
import fittoring.mentoring.infra.exception.InfraErrorMessage;
import fittoring.mentoring.infra.exception.S3UploadException;
import fittoring.mentoring.infra.image.S3Uploader;
import fittoring.mentoring.infra.image.VariantUploadResult;
import fittoring.mentoring.infra.image.policy.ImagePolicyRegistry;
import fittoring.mentoring.infra.image.policy.ImageTypePolicy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final S3Uploader s3Uploader;
    private final ImagePolicyRegistry imagePolicyRegistry;

    public Optional<Image> save(ImageType type, Long relationId, String imageUrl) {
        return Optional.of(imageRepository.save(new Image(
                imageUrl,
                type,
                relationId
        )));
    }

    public List<Image> saveAll(ImageType type, Long relationId, List<String> imagesUrl) {
        List<Image> images = new ArrayList<>();
        for (String imageUrl : imagesUrl) {
            images.add(new Image(imageUrl, type, relationId));
        }
        return imageRepository.saveAll(images);
    }

    // TODO: api 작업 후 deprecated
    public List<Image> uploadImageToS3(MultipartFile imageFile, String dir, ImageType type, Long relationId) {
        try {
            ImageTypePolicy policy = imagePolicyRegistry.get(type);
            String baseName = UUID.randomUUID().toString();
            List<Image> images = new ArrayList<>();

            for (ImageVariant variant : policy.variants()) {
                int maxWidth = policy.maxWidth(variant);
                VariantUploadResult uploaded = s3Uploader.uploadVariant(
                        imageFile,
                        dir,
                        variant,
                        maxWidth,
                        baseName
                );
                images.add(new Image(uploaded.originalUrl(), type, uploaded.variant(), relationId));
            }
            return imageRepository.saveAll(images);
        } catch (IOException e) {
            throw new S3UploadException(InfraErrorMessage.S3_UPLOAD_ERROR.getMessage());
        }
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
