package fittoring.application.image.service;

import fittoring.config.S3Properties;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.ImageVariant;
import fittoring.application.image.service.dto.IssuedPresignedDto;
import fittoring.infrastructure.image.ContentType;
import fittoring.infrastructure.image.KeyBuilder;
import fittoring.application.image.presentation.dto.response.PresignedIssueResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@RequiredArgsConstructor
@Component
public class PresignedUrlService {

    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final S3Properties properties;

    public PresignedIssueResponse issuePresignedUrl(
            IssuedPresignedDto dto
    ) {
        String baseName = UUID.randomUUID().toString();
        String key = KeyBuilder.buildKey(ImageType.getDir(dto.imageType()), ImageVariant.DEFAULT, baseName,
                "." + dto.extension());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(properties.getBucketName())
                .key(key)
                .contentType(ContentType.of(dto.extension()))
                .cacheControl("public, max-age=31536000, immutable")
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(3))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presigned = presigner.presignPutObject(presignRequest);

        return new PresignedIssueResponse(
                presigned.url().toString(),
                LocalDateTime.ofInstant(
                        presigned.expiration(),
                        ZoneId.of("Asia/Seoul")
                )
        );
    }

    public boolean isObjectExistsFromKey(String key) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(key)
                    .build();
            s3Client.headObject(request);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isObjectExistsFromUrl(String url) {
        try {
            String key = KeyBuilder.extractFromUrl(url);
            return isObjectExistsFromKey(key);
        } catch (Exception e) {
            return false;
        }
    }
}
