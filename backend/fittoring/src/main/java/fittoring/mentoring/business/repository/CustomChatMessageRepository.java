package fittoring.mentoring.business.repository;

import fittoring.mentoring.Cursor;
import fittoring.mentoring.business.model.SortKey;
import fittoring.mentoring.business.service.dto.chat.ChatMessagePaginationResult;

public interface CustomChatMessageRepository {

    ChatMessagePaginationResult findChatMessagesWithPagination(Long chatRoomId, SortKey sortKey, Cursor cursor);

}
