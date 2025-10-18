package fittoring.mentoring.business.repository;

import fittoring.mentoring.Cursor;
import fittoring.mentoring.business.service.dto.chat.ChatMessagePaginationResultDto;

public interface CustomChatMessageRepository {

    ChatMessagePaginationResultDto findChatMessagesWithPagination(Long chatRoomId, Cursor cursor);
}
