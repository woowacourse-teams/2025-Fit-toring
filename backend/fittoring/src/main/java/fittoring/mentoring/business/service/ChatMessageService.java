package fittoring.mentoring.business.service;

import fittoring.mentoring.Cursor;
import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.ChatRoomNotFoundException;
import fittoring.mentoring.business.exception.UnauthorizedChatRoomAccessException;
import fittoring.mentoring.business.model.ChatMessage;
import fittoring.mentoring.business.model.ChatRoom;
import fittoring.mentoring.business.repository.ChatMessageRepository;
import fittoring.mentoring.business.repository.ChatRoomRepository;
import fittoring.mentoring.business.service.dto.chat.ChatMessagePaginationResult;
import fittoring.mentoring.presentation.dto.chat.request.ChatMessageRequest;
import fittoring.mentoring.presentation.dto.chat.response.ChatMessagePaginationResponse;
import fittoring.mentoring.presentation.dto.chat.response.ChatMessageResponse;
import fittoring.util.CursorCodec;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ChatMessageResponse registerMessage(ChatMessageRequest request, Long memberId) {
        ChatMessage chatMessage = new ChatMessage(request.chatRoomId(), memberId, request.content());
        chatMessageRepository.save(chatMessage);
        return ChatMessageResponse.from(chatMessage, request.tempId());
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

        ChatMessagePaginationResult paginationResult = chatMessageRepository.findChatMessagesWithPagination(
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
                        () -> new ChatRoomNotFoundException(BusinessErrorMessage.CHAT_ROOM_NOT_FOUNT.getMessage())
                );
    }

    private void validateParticipant(Long memberId, ChatRoom chatRoom) {
        if (!chatRoom.hasParticipant(memberId)) {
            throw new UnauthorizedChatRoomAccessException(
                    BusinessErrorMessage.UNAUTHORIZED_CHAT_ROOM_ACCESS.getMessage()
            );
        }
    }

    private List<ChatMessageResponse> getChatMessageResponses(ChatMessagePaginationResult paginationResult) {
        return paginationResult.chatMessages()
                .stream()
                .map(ChatMessageResponse::fromHistory)
                .toList();
    }
}
