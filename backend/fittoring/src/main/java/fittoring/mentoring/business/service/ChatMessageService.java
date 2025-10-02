package fittoring.mentoring.business.service;

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
    public ChatMessageResponse registerMessage(ChatMessageRequest request, Long memberId) {
        ChatMessage chatMessage = new ChatMessage(request.chatRoomId(), memberId, request.content());
        chatMessageRepository.save(chatMessage);
        return ChatMessageResponse.from(chatMessage, request.tempId());
    }
}
