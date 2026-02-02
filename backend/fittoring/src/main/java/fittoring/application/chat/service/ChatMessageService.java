package fittoring.application.chat.service;

import fittoring.application.chat.presentation.dto.request.ChatMessageRequest;
import fittoring.application.chat.presentation.dto.response.ChatMessagePaginationResponse;
import fittoring.application.chat.presentation.dto.response.ChatMessageResponse;
import fittoring.application.chat.repository.ChatMessageRepository;
import fittoring.application.chat.repository.ChatRoomRepository;
import fittoring.application.chat.service.dto.ChatMessagePaginationResultDto;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ChatRoomNotFoundException;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.exception.UnauthorizedChatRoomAccessException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.notification.service.NotificationService;
import fittoring.domain.model.ChatMessage;
import fittoring.domain.model.ChatRoom;
import fittoring.domain.model.Notification;
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

    @Transactional
    public ChatMessageResponse registerMessage(Long chatRoomId, ChatMessageRequest request, Long senderId) {
        ChatRoom chatRoom = getChatRoom(chatRoomId);
        validateParticipant(senderId, chatRoom);

        ChatMessage chatMessage = new ChatMessage(chatRoomId, senderId, request.content());
        chatMessageRepository.save(chatMessage);

        log.info("채팅을 보낸 사람 id: {}", senderId);
        Long opponentId = chatRoom.getOpponentIdOf(senderId);
        sendNewMessageNotification(chatRoomId, senderId, opponentId, chatMessage);
        return ChatMessageResponse.from(chatMessage, request.tempId());
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
    public Map<Long, ChatMessage> findChatRoomLastChatMessageMapping(List<ChatRoom> chatRooms) {
        List<Long> chatRoomIds = chatRooms.stream()
                .map(ChatRoom::getId)
                .toList();
        return chatMessageRepository.findChatRoomLastChatMessageMapping(chatRoomIds);
    }
}
