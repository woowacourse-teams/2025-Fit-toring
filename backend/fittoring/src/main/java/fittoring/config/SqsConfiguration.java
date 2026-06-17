package fittoring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.ListenerMode;
import io.awspring.cloud.sqs.listener.QueueNotFoundStrategy;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import io.awspring.cloud.sqs.operations.TemplateContentBasedDeduplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
@Profile({"!local & !test"})
public class SqsConfiguration {

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${spring.cloud.aws.region}")
    private String region;

    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    @Bean
    public SqsTemplate sqsTemplate(SqsAsyncClient sqsAsyncClient, ObjectMapper objectMapper) {
        return SqsTemplate.builder()
                .sqsAsyncClient(sqsAsyncClient)
                .configureDefaultConverter(converter -> converter.setObjectMapper(objectMapper))
                .configure(options -> options
                        .queueNotFoundStrategy(QueueNotFoundStrategy.FAIL)
                        .contentBasedDeduplication(TemplateContentBasedDeduplication.DISABLED))
                .build();
    }

    /**
     * SMS 빠른 길 전용 배치 리스너 팩토리.
     * 수신당 최대 10건을 묶어(List) 전달해 한 번의 claim·발송으로 처리한다.
     */
    @Bean
    public SqsMessageListenerContainerFactory<Object> smsDispatchSqsListenerContainerFactory(
            SqsAsyncClient sqsAsyncClient
    ) {
        return SqsMessageListenerContainerFactory.builder()
                .sqsAsyncClient(sqsAsyncClient)
                .configure(options -> options
                        .listenerMode(ListenerMode.BATCH)
                        .maxMessagesPerPoll(10)
                        .maxConcurrentMessages(10))
                .build();
    }
}
