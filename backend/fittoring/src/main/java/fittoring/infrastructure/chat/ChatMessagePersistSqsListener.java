package fittoring.infrastructure.chat;

import fittoring.application.chat.service.ChatMessagePersistenceService;
import fittoring.application.chat.service.dto.ChatMessagePersistEventDto;
import io.awspring.cloud.sqs.annotation.SqsListener;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Profile({"!local & !test"})
@Component
public class ChatMessagePersistSqsListener {

    private final ChatMessagePersistenceService chatMessagePersistenceService;

    @SqsListener("${aws.sqs.chat-message-persist-queue}")
    public void handle(@Valid @Payload ChatMessagePersistEventDto event) {
        chatMessagePersistenceService.persist(event);
    }
}
