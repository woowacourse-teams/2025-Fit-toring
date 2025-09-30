package fittoring.mentoring.business.service;

import fittoring.config.S3Properties;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.ImageVariant;
import fittoring.mentoring.business.service.dto.IssuedPresignedDto;
import fittoring.mentoring.infra.image.ContentType;
import fittoring.mentoring.infra.image.KeyBuilder;
import fittoring.mentoring.presentation.dto.PresignedIssueResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@RequiredArgsConstructor
@Component
public class PresignedUrlService {

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
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(putObjectRequest)
                .build();

        return new PresignedIssueResponse(
                presigner.presignPutObject(presignRequest).url().toString(),
                LocalDateTime.ofInstant(
                        presigner.presignPutObject(presignRequest).expiration(),
                        ZoneId.of("Asia/Seoul")
                )
        );
    }
}
