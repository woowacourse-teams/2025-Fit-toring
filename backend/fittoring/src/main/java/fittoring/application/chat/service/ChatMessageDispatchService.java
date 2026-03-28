package fittoring.application.chat.service;

import fittoring.application.chat.presentation.dto.request.ChatMessageRequest;
import fittoring.application.chat.service.dto.ChatMessageAcceptedResultDto;
import fittoring.application.chat.service.dto.ChatMessagePersistEventDto;
import fittoring.application.chat.service.port.ChatMessagePersistEventPublisher;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ImageNotFoundException;
import fittoring.application.image.service.PresignedUrlService;
import fittoring.domain.model.ChatMessageType;
import fittoring.infrastructure.image.KeyBuilder;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatMessageDispatchService {

    private final ChatRoomService chatRoomService;
    private final ChatMessagePersistEventPublisher eventPublisher;
    private final PresignedUrlService presignedUrlService;
    private final KeyBuilder keyBuilder;

    public ChatMessageAcceptedResultDto dispatch(Long chatRoomId, ChatMessageRequest request, Long senderId) {
        chatRoomService.getAccessibleChatRoom(chatRoomId, senderId);

        String messageId = UUID.randomUUID().toString();
        LocalDateTime requestedAt = LocalDateTime.now();
        String normalizedContent = normalizeContent(request);

        ChatMessagePersistEventDto event = ChatMessagePersistEventDto.of(
                messageId,
                chatRoomId,
                senderId,
                request,
                normalizedContent,
                requestedAt
        );
        eventPublisher.publish(event);

        return toAcceptedResult(event);
    }

    private String normalizeContent(ChatMessageRequest request) {
        if (isImageType(request.messageType())) {
            String imageUrl = request.content();
            validateImageExists(imageUrl);

            return keyBuilder.extractKeyFromUrl(imageUrl);
        }

        return request.content();
    }

    private void validateImageExists(String imageUrl) {
        if (!presignedUrlService.isObjectExistsFromUrl(imageUrl)) {
            throw new ImageNotFoundException(BusinessErrorMessage.IMAGE_NOT_FOUND.getMessage());
        }
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
