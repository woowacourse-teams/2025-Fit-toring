package fittoring.application.chat.service;

import fittoring.application.chat.presentation.dto.request.ChatImageMessageRequest;
import fittoring.application.chat.presentation.dto.request.ChatTextMessageRequest;
import fittoring.application.chat.service.dto.ChatMessageAcceptedResultDto;
import fittoring.application.chat.service.dto.ChatMessagePersistEventDto;
import fittoring.application.chat.service.port.ChatMessagePersistEventPublisher;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatMessageDispatchService {

    private final ChatMessagePersistEventPublisher eventPublisher;

    public ChatMessageAcceptedResultDto dispatchText(
            Long chatRoomId,
            ChatTextMessageRequest request,
            Long senderId
    ) {
        String messageId = UUID.randomUUID().toString();
        LocalDateTime requestedAt = LocalDateTime.now();

        ChatMessagePersistEventDto event = ChatMessagePersistEventDto.text(
                messageId,
                chatRoomId,
                senderId,
                request,
                requestedAt
        );
        eventPublisher.publish(event);

        return ChatMessageAcceptedResultDto.text(event);
    }

    public ChatMessageAcceptedResultDto dispatchImage(
            Long chatRoomId,
            ChatImageMessageRequest request,
            Long senderId
    ) {
        throw new UnsupportedOperationException("IMAGE 메시지 확정은 Redis 티켓 검증 단계에서 구현합니다.");
    }
}
