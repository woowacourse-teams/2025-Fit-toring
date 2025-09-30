package fittoring.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "aws.s3")
public class S3Properties {

    private final String region;
    private final String bucketName;
}
