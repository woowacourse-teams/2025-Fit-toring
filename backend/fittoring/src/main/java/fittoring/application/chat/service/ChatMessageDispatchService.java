package fittoring.application.chat.service;

import fittoring.application.chat.presentation.dto.request.ChatImageMessageRequest;
import fittoring.application.chat.presentation.dto.request.ChatTextMessageRequest;
import fittoring.application.chat.repository.ChatImageUploadTicketRepository;
import fittoring.application.chat.service.dto.ChatMessageAcceptedResultDto;
import fittoring.application.chat.service.dto.ChatMessagePersistEventDto;
import fittoring.application.chat.service.port.ChatMessagePersistEventPublisher;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ImageNotFoundException;
import fittoring.application.exception.InvalidChatImageUploadTicketException;
import fittoring.application.image.service.PresignedUrlService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatMessageDispatchService {

    private final ChatMessagePersistEventPublisher eventPublisher;
    private final ChatImageUploadTicketRepository ticketRepository;
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

        return ChatMessageAcceptedResultDto.text(event);
    }

    public ChatMessageAcceptedResultDto dispatchImage(
            Long chatRoomId,
            ChatImageMessageRequest request,
            Long senderId
    ) {
        String s3Key = ticketRepository.consume(request.uploadId(), senderId, chatRoomId)
                .orElseThrow(() -> new InvalidChatImageUploadTicketException(
                        BusinessErrorMessage.INVALID_CHAT_IMAGE_UPLOAD_TICKET.getMessage()));

        if (!presignedUrlService.isObjectExistsFromKey(s3Key)) {
            throw new ImageNotFoundException(BusinessErrorMessage.IMAGE_NOT_FOUND.getMessage());
        }

        String messageId = UUID.randomUUID().toString();
        LocalDateTime requestedAt = LocalDateTime.now();

        ChatMessagePersistEventDto event = ChatMessagePersistEventDto.image(
                messageId,
                chatRoomId,
                senderId,
                request.tempId(),
                s3Key,
                requestedAt
        );
        eventPublisher.publish(event);

        String originalImageUrl = presignedUrlService.issueGetPresignedUrl(s3Key);
        return ChatMessageAcceptedResultDto.image(event, originalImageUrl);
    }
}
