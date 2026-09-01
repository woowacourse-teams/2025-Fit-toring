package fittoring.application.chat.service;

import fittoring.application.chat.presentation.dto.response.ChatMessagePaginationResponse;
import fittoring.application.chat.presentation.dto.response.ChatMessageResponse;
import fittoring.application.chat.repository.ChatMessageRepository;
import fittoring.application.chat.repository.ChatRoomRepository;
import fittoring.application.chat.service.dto.ChatMessagePaginationResultDto;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ChatMessageNotFoundException;
import fittoring.application.exception.ChatMessageNotImageException;
import fittoring.application.exception.ChatRoomNotFoundException;
import fittoring.application.exception.UnauthorizedChatMessageAccessException;
import fittoring.application.exception.UnauthorizedChatRoomAccessException;
import fittoring.application.image.presentation.dto.response.ImageUrlResponse;
import fittoring.application.image.service.ImageService;
import fittoring.application.image.service.PresignedUrlService;
import fittoring.domain.model.ChatMessage;
import fittoring.domain.model.ChatMessageType;
import fittoring.domain.model.ChatRoom;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.ImageVariant;
import fittoring.util.Cursor;
import fittoring.util.CursorCodec;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatMessageQueryService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final PresignedUrlService presignedUrlService;
    private final ImageService imageService;

    @Transactional(readOnly = true)
    public ChatMessagePaginationResponse findChatMessages(
            Long chatRoomId,
            Long memberId,
            String cursorCode
    ) {
        ChatRoom chatRoom = getChatRoom(chatRoomId);
        validateParticipant(memberId, chatRoom);

        Cursor cursor = CursorCodec.decode(cursorCode);

        ChatMessagePaginationResultDto paginationResult = chatMessageRepository.findChatMessagesWithPagination(
                chatRoomId,
                cursor
        );

        List<ChatMessageResponse> responses = getChatMessageResponses(paginationResult);

        return new ChatMessagePaginationResponse(
                responses,
                paginationResult.nextCursorCode(),
                paginationResult.hasNext()
        );
    }

    private ChatRoom getChatRoom(Long chatroomId) {
        return chatRoomRepository.findById(chatroomId)
                .orElseThrow(
                        () -> new ChatRoomNotFoundException(BusinessErrorMessage.CHAT_ROOM_NOT_FOUND.getMessage())
                );
    }

    private void validateParticipant(Long memberId, ChatRoom chatRoom) {
        if (chatRoom.isNonParticipant(memberId)) {
            throw new UnauthorizedChatRoomAccessException(
                    BusinessErrorMessage.UNAUTHORIZED_CHAT_ROOM_ACCESS.getMessage()
            );
        }
    }

    private List<ChatMessageResponse> getChatMessageResponses(ChatMessagePaginationResultDto paginationResult) {
        List<ChatMessage> messages = paginationResult.chatMessages();

        List<Long> imageMessageIds = messages.stream()
                .filter(msg -> msg.getMessageType() == ChatMessageType.IMAGE)
                .map(ChatMessage::getId)
                .toList();

        Set<Long> thumbnailExistsIds = new HashSet<>(
                imageService.findThumbnailExistsIds(imageMessageIds, ImageType.CHAT)
        );

        return messages.stream()
                .map(msg -> {
                    if (msg.getMessageType() == ChatMessageType.IMAGE) {
                        return toImageResponse(msg, thumbnailExistsIds.contains(msg.getId()));
                    }
                    return ChatMessageResponse.from(msg);
                })
                .toList();
    }

    private ChatMessageResponse toImageResponse(ChatMessage chatMessage, boolean hasThumbnail) {
        ImageUrlResponse urls = presignedUrlService.issueGetUrlWithThumbnail(chatMessage.getContent(), hasThumbnail);
        return ChatMessageResponse.from(chatMessage, null, urls.thumbnailUrl(), urls.originalImageUrl());
    }

    @Transactional(readOnly = true)
    public ImageUrlResponse reissueImageUrl(Long chatRoomId, Long messageId, Long memberId) {
        ChatRoom chatRoom = getChatRoom(chatRoomId);
        validateParticipant(memberId, chatRoom);

        ChatMessage chatMessage = getImageChatMessage(messageId, chatRoomId);

        boolean hasThumbnail = imageService.findThumbnail(ImageType.CHAT, messageId)
                .map(img -> img.getImageVariant() == ImageVariant.THUMBNAIL)
                .orElse(false);

        return presignedUrlService.issueGetUrlWithThumbnail(chatMessage.getContent(), hasThumbnail);
    }

    private ChatMessage getImageChatMessage(Long messageId, Long chatRoomId) {
        ChatMessage chatMessage = getChatMessage(messageId);
        validateChatMessageBelongsToRoom(chatMessage, chatRoomId);
        validateImageType(chatMessage);
        return chatMessage;
    }

    private ChatMessage getChatMessage(Long messageId) {
        return chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ChatMessageNotFoundException(
                        BusinessErrorMessage.CHAT_MESSAGE_NOT_FOUND.getMessage()));
    }

    private void validateChatMessageBelongsToRoom(ChatMessage chatMessage, Long chatRoomId) {
        if (!chatMessage.getChatRoomId().equals(chatRoomId)) {
            throw new UnauthorizedChatMessageAccessException(
                    BusinessErrorMessage.UNAUTHORIZED_CHAT_MESSAGE_ACCESS.getMessage());
        }
    }

    private void validateImageType(ChatMessage chatMessage) {
        if (chatMessage.getMessageType() != ChatMessageType.IMAGE) {
            throw new ChatMessageNotImageException(BusinessErrorMessage.CHAT_MESSAGE_NOT_IMAGE.getMessage());
        }
    }

}
