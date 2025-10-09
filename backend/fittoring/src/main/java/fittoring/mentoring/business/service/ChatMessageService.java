package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.ChatRoomNotFoundException;
import fittoring.mentoring.business.exception.UnauthorizedChatRoomAccessException;
import fittoring.mentoring.business.model.ChatMessage;
import fittoring.mentoring.business.model.ChatRoom;
import fittoring.mentoring.business.repository.ChatMessageRepository;
import fittoring.mentoring.business.repository.ChatRoomRepository;
import fittoring.mentoring.presentation.dto.chat.request.ChatMessageRequest;
import fittoring.mentoring.presentation.dto.chat.response.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ChatMessageResponse registerMessage(Long chatRoomId, ChatMessageRequest request, Long senderId) {
        ChatRoom chatRoom = getChatRoom(chatRoomId);
        validateParticipant(senderId, chatRoom);

        ChatMessage chatMessage = new ChatMessage(chatRoomId, senderId, request.content());
        chatMessageRepository.save(chatMessage);
        return ChatMessageResponse.from(chatMessage, request.tempId());
    }

    private ChatRoom getChatRoom(Long chatroomId) {
        return chatRoomRepository.findById(chatroomId)
                .orElseThrow(
                        () -> new ChatRoomNotFoundException(BusinessErrorMessage.CHAT_ROOM_NOT_FOUNT.getMessage())
                );
    }

    private void validateParticipant(Long senderId, ChatRoom chatRoom) {
        if (!chatRoom.hasParticipant(senderId)) {
            throw new UnauthorizedChatRoomAccessException(
                    BusinessErrorMessage.UNAUTHORIZED_CHAT_ROOM_ACCESS.getMessage()
            );
        }
    }
}
