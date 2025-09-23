package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.repository.ImageRepository;
import fittoring.mentoring.infra.S3Uploader;
import fittoring.mentoring.infra.exception.InfraErrorMessage;
import fittoring.mentoring.infra.exception.S3UploadException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final S3Uploader s3Uploader;

    public Image uploadImageToS3(MultipartFile imageFile, String dir, ImageType type, Long id) {
        try {
            String s3Url = s3Uploader.upload(imageFile, dir);
            Image image = new Image(s3Url, type, id);
            deleteByImageTypeAndRelationId(type, id);
            return saveImage(image);
        } catch (IOException e) {
            throw new S3UploadException(InfraErrorMessage.S3_UPLOAD_ERROR.getMessage());
        }
    }

    private Image saveImage(Image image){
        return imageRepository.save(image);
    }

    public Optional<Image> findByImageTypeAndRelationId(ImageType imageType, Long relationId) {
        return imageRepository.findByImageTypeAndRelationId(imageType, relationId);
    }

    public List<Image> findByRelationIdsAndImageType(List<Long> certificateIds, ImageType imageType) {
        return imageRepository.findByRelationIdsAndImageType(certificateIds, imageType);
    }

    public void deleteByImageTypeAndRelationId(ImageType imageType, Long relationId) {
        imageRepository.deleteByImageTypeAndRelationId(imageType, relationId);
    }
}
