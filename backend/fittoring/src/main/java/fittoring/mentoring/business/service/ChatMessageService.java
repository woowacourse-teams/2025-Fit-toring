package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.ChatRoomNotFoundException;
import fittoring.mentoring.business.model.ChatMessage;
import fittoring.mentoring.business.repository.ChatMessageRepository;
import fittoring.mentoring.presentation.dto.chat.request.ChatMessageRequest;
import fittoring.mentoring.presentation.dto.chat.response.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ChatMessageResponse registerMessage(Long chatRoomId, ChatMessageRequest request, Long senderId) {
        validateExistsChatRoom(chatRoomId);

        ChatMessage chatMessage = new ChatMessage(chatRoomId, senderId, request.content());
        chatMessageRepository.save(chatMessage);
        return ChatMessageResponse.from(chatMessage, request.tempId());
    }

    private void validateExistsChatRoom(Long chatRoomId) {
        if (!chatMessageRepository.existsById(chatRoomId)) {
            throw new ChatRoomNotFoundException(BusinessErrorMessage.CHAT_ROOM_NOT_FOUNT.getMessage());
        }
    }
}
