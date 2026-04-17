package fittoring.application.chat.service.port;

import fittoring.application.chat.service.dto.ChatMessagePersistEventDto;

public interface ChatMessagePersistEventPublisher {

    void publish(ChatMessagePersistEventDto event);
}
