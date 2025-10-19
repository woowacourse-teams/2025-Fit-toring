package fittoring.infrastructure;

import fittoring.application.image.service.ImageSessionService;
import fittoring.infrastructure.dto.ImageReadyMessageDto;
import io.awspring.cloud.sqs.annotation.SqsListener;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Profile({"!local & !test"})
@Component
public class ImageSqsListener {

    private final ImageSessionService imageSessionService;

    @SqsListener("${aws.sqs.image-queue}")
    public void handle(@Valid @Payload ImageReadyMessageDto message) {
        imageSessionService.imageProcessor(message);
    }
}
