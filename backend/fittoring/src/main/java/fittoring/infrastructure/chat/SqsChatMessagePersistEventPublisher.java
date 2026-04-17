package fittoring.infrastructure.chat;

import fittoring.application.chat.service.dto.ChatMessagePersistEventDto;
import fittoring.application.chat.service.port.ChatMessagePersistEventPublisher;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Profile({"!local & !test"})
public class SqsChatMessagePersistEventPublisher implements ChatMessagePersistEventPublisher {

    private final SqsTemplate sqsTemplate;

    @Value("${aws.sqs.chat-message-persist-queue}")
    private String queueName;

    @Override
    public void publish(ChatMessagePersistEventDto event) {
        sqsTemplate.send(options -> options
                .queue(queueName)
                .payload(event)
                .messageGroupId(String.valueOf(event.chatRoomId()))
                .messageDeduplicationId(event.messageId()));
    }
}
