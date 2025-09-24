package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.repository.ImageRepository;
import fittoring.mentoring.infra.exception.InfraErrorMessage;
import fittoring.mentoring.infra.exception.S3UploadException;
import fittoring.mentoring.business.model.ImageVariant;
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

    public Optional<Image> findByImageTypeAndRelationId(ImageType imageType, Long relationId) {
        return imageRepository.findByImageTypeAndRelationId(imageType, relationId);
    }

    public List<Image> uploadImageToS3(MultipartFile imageFile, String dir, ImageType type, Long relationId) {
        try {
            ImageTypePolicy policy = imagePolicyRegistry.get(type);
            List<ImageVariant> variants = policy.variants();
            deleteByImageTypeAndRelationId(type, relationId);
            String baseName = UUID.randomUUID().toString();
            List<Image> results = new ArrayList<>();
            for (ImageVariant variant : variants) {
                int maxWidth = policy.maxWidth(variant);
                VariantUploadResult uploaded = s3Uploader.uploadVariant(
                        imageFile,
                        dir,
                        variant,
                        maxWidth,
                        baseName
                );
                Image row = new Image(uploaded.originalUrl(), type, relationId);
                results.add(saveImage(row));
            }
            return results;
        } catch (IOException e) {
            throw new S3UploadException(InfraErrorMessage.S3_UPLOAD_ERROR.getMessage());
        }
    }

    private Image saveImage(Image image) {
        return imageRepository.save(image);
    }

    public void deleteByImageTypeAndRelationId(ImageType imageType, Long relationId) {
        imageRepository.deleteByImageTypeAndRelationId(imageType, relationId);
    }
}
