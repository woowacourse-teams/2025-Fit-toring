package fittoring.application.chat.service;

import fittoring.application.chat.presentation.dto.request.ChatMessageRequest;
import fittoring.application.chat.presentation.dto.response.ChatMessagePaginationResponse;
import fittoring.application.chat.presentation.dto.response.ChatMessageResponse;
import fittoring.application.chat.repository.ChatMessageRepository;
import fittoring.application.chat.repository.ChatRoomRepository;
import fittoring.application.chat.service.dto.ChatMessagePaginationResultDto;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ChatRoomNotFoundException;
import fittoring.application.exception.ImageNotFoundException;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.exception.UnauthorizedChatRoomAccessException;
import fittoring.application.image.service.ImageService;
import fittoring.application.image.service.PresignedUrlService;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.notification.service.NotificationService;
import fittoring.domain.model.ChatMessage;
import fittoring.domain.model.ChatMessageType;
import fittoring.domain.model.ChatRoom;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.Notification;
import fittoring.infrastructure.image.KeyBuilder;
import fittoring.util.Cursor;
import fittoring.util.CursorCodec;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final NotificationService notificationService;
    private final MemberRepository memberRepository;
    private final PresignedUrlService presignedUrlService;
    private final ImageService imageService;
    private final KeyBuilder keyBuilder;

    @Transactional
    public ChatMessageResponse registerMessage(Long chatRoomId, ChatMessageRequest request, Long senderId) {
        ChatRoom chatRoom = getChatRoom(chatRoomId);
        validateParticipant(senderId, chatRoom);

        if (request.messageType() == ChatMessageType.IMAGE) {
            return registerImageMessage(chatRoom, request, senderId);
        }
        return registerTextMessage(chatRoom, request, senderId);
    }

    private ChatMessageResponse registerTextMessage(ChatRoom chatRoom, ChatMessageRequest request, Long senderId) {
        ChatMessage chatMessage = new ChatMessage(chatRoom.getId(), senderId, request.content());
        chatMessageRepository.save(chatMessage);

        log.info("채팅을 보낸 사람 id: {}", senderId);
        Long opponentId = chatRoom.getOpponentIdOf(senderId);
        sendNewMessageNotification(chatRoom.getId(), senderId, opponentId, chatMessage);
        return ChatMessageResponse.from(chatMessage, request.tempId());
    }

    private ChatMessageResponse registerImageMessage(ChatRoom chatRoom, ChatMessageRequest request, Long senderId) {
        String imageUrl = request.content();

        if (!presignedUrlService.isObjectExistsFromUrl(imageUrl)) {
            throw new ImageNotFoundException(BusinessErrorMessage.IMAGE_NOT_FOUND.getMessage());
        }

        String imageKey = keyBuilder.extractKeyFromUrl(imageUrl);

        ChatMessage chatMessage = new ChatMessage(chatRoom.getId(), senderId, imageKey, ChatMessageType.IMAGE);
        chatMessageRepository.save(chatMessage);

        imageService.save(ImageType.CHAT, chatMessage.getId(), imageUrl);

        String originalUrl = presignedUrlService.issueGetPresignedUrl(imageKey);

        log.info("이미지 채팅을 보낸 사람 id: {}", senderId);
        Long opponentId = chatRoom.getOpponentIdOf(senderId);
        sendNewMessageNotification(chatRoom.getId(), senderId, opponentId, chatMessage);

        return ChatMessageResponse.imageFrom(chatMessage, request.tempId(), null, originalUrl);
    }

    private void sendNewMessageNotification(Long chatRoomId, Long senderId, Long opponentId, ChatMessage chatMessage) {
        String senderName = memberRepository.findNameById(senderId)
                .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
        Notification notification = new Notification(senderName, chatMessage.getContent());
        notification.putData("chatRoomId", String.valueOf(chatRoomId));
        notificationService.sendNotification(opponentId, notification);
    }

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
        return paginationResult.chatMessages()
                .stream()
                .map(ChatMessageResponse::fromHistory)
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<Long, ChatMessage> findAllLastMessagesByRoomIds(List<ChatRoom> chatRooms) {
        List<Long> chatRoomIds = chatRooms.stream()
                .map(ChatRoom::getId)
                .toList();
        return chatMessageRepository.findAllLastMessagesByRoomIds(chatRoomIds);
    }
}
