package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.repository.ImageRepository;
import fittoring.mentoring.infra.exception.InfraErrorMessage;
import fittoring.mentoring.infra.exception.S3UploadException;
import fittoring.mentoring.infra.image.ImageVariant;
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

    /**
     * Finds an Image by its type and the related entity's id.
     *
     * @param imageType the type of the image
     * @param relationId the id of the related entity the image is associated with
     * @return an Optional containing the Image if present, otherwise an empty Optional
     */
    public Optional<Image> findByImageTypeAndRelationId(ImageType imageType, Long relationId) {
        return imageRepository.findByImageTypeAndRelationId(imageType, relationId);
    }

    /**
     * Uploads image variants to S3 according to the ImageType policy, persists one Image record per variant,
     * and returns the saved Image entities.
     *
     * The method:
     * - Retrieves the ImageTypePolicy for the given type and obtains its variants.
     * - Deletes any existing images for (type, relationId).
     * - Generates a UUID base name and uploads each variant via the S3 uploader using the policy's max width.
     * - Persists an Image row for each uploaded variant and returns the list of saved images.
     *
     * @param imageFile  the uploaded multipart image file to process and upload
     * @param dir        S3 directory/prefix where variants should be uploaded
     * @param type       the ImageType whose policy determines variants and sizing
     * @param relationId identifier of the related entity the images are associated with
     * @return a list of persisted Image entities, one per uploaded variant
     * @throws S3UploadException when an I/O error occurs during upload
     */
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

    /**
     * Persists the given Image entity and returns the managed instance.
     *
     * <p>The returned Image will reflect any changes applied by the persistence layer
     * (for example, generated identifiers or timestamps).
     *
     * @return the saved (managed) Image entity
     */
    private Image saveImage(Image image) {
        return imageRepository.save(image);
    }

    public void deleteByImageTypeAndRelationId(ImageType imageType, Long relationId) {
        imageRepository.deleteByImageTypeAndRelationId(imageType, relationId);
    }
}
