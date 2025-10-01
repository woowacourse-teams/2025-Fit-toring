package fittoring.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@RequiredArgsConstructor
@Validated
@ConfigurationProperties(prefix = "aws.s3")
public class S3Properties {

    @NotBlank
    private final String region;

    @NotBlank
    private final String bucketName;
}
