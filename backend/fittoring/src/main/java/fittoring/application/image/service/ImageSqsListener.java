package fittoring.application.image.service;

import fittoring.application.image.presentation.dto.request.ImageReadyMessageRequest;
import io.awspring.cloud.sqs.annotation.SqsListener;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ImageSqsListener {

    private final ImageSessionService imageSessionService;

    @SqsListener("fittoring-image-queue")
    public void handle(@Valid @Payload ImageReadyMessageRequest msg) {
    }
}
