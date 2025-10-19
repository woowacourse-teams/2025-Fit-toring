package fittoring.application.chatroom.service;

import fittoring.application.chatroom.presentation.dto.request.ChatMessageRequest;
import fittoring.application.chatroom.presentation.dto.response.ChatMessagePaginationResponse;
import fittoring.application.chatroom.presentation.dto.response.ChatMessageResponse;
import fittoring.application.chatroom.repository.ChatMessageRepository;
import fittoring.application.chatroom.repository.ChatRoomRepository;
import fittoring.application.chatroom.service.dto.ChatMessagePaginationResultDto;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.domain.model.ChatMessage;
import fittoring.domain.model.ChatRoom;
import fittoring.mentoring.business.exception.ChatRoomNotFoundException;
import fittoring.mentoring.business.exception.UnauthorizedChatRoomAccessException;
import fittoring.util.Cursor;
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
    public ChatMessageResponse registerMessage(Long chatRoomId, ChatMessageRequest request, Long senderId) {
        ChatRoom chatRoom = getChatRoom(chatRoomId);
        validateParticipant(senderId, chatRoom);

        ChatMessage chatMessage = new ChatMessage(chatRoomId, senderId, request.content());
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

}
