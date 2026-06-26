package fittoring.application.chat.service;

import fittoring.application.chat.presentation.dto.request.ChatImageMessageRequest;
import fittoring.application.chat.presentation.dto.request.ChatTextMessageRequest;
import fittoring.application.chat.service.dto.ChatMessageAcceptedResultDto;
import fittoring.application.chat.service.dto.ChatMessagePersistEventDto;
import fittoring.application.chat.service.port.ChatMessagePersistEventPublisher;
import fittoring.application.image.service.PresignedUrlService;
import fittoring.domain.model.ChatMessageType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatMessageDispatchService {

    private final ChatMessagePersistEventPublisher eventPublisher;
    private final PresignedUrlService presignedUrlService;

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

        return toAcceptedResult(event);
    }

    public ChatMessageAcceptedResultDto dispatchImage(
            Long chatRoomId,
            ChatImageMessageRequest request,
            Long senderId
    ) {
        throw new UnsupportedOperationException("IMAGE 메시지 확정은 Redis 티켓 검증 단계에서 구현합니다.");
    }

    private ChatMessageAcceptedResultDto toAcceptedResult(ChatMessagePersistEventDto event) {
        if (isImageType(event.messageType())) {
            String originalImageUrl = presignedUrlService.issueGetPresignedUrl(event.content());
            return new ChatMessageAcceptedResultDto(
                    event.messageId(),
                    event.tempId(),
                    event.chatRoomId(),
                    event.senderId(),
                    null,
                    event.messageType(),
                    null,
                    originalImageUrl,
                    event.requestedAt()
            );
        }

        return new ChatMessageAcceptedResultDto(
                event.messageId(),
                event.tempId(),
                event.chatRoomId(),
                event.senderId(),
                event.content(),
                event.messageType(),
                null,
                null,
                event.requestedAt()
        );
    }

    private boolean isImageType(ChatMessageType messageType) {
        return messageType == ChatMessageType.IMAGE;
    }
}
