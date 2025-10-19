package fittoring.application.mentoring.repository;

import fittoring.application.chatroom.service.dto.ChatMessagePaginationResultDto;
import fittoring.util.Cursor;

public interface CustomChatMessageRepository {

    ChatMessagePaginationResultDto findChatMessagesWithPagination(Long chatRoomId, Cursor cursor);
}
