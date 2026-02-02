package fittoring.application.chat.repository;

import fittoring.application.chat.service.dto.ChatMessagePaginationResultDto;
import fittoring.domain.model.ChatMessage;
import fittoring.util.Cursor;

import java.util.List;
import java.util.Map;

public interface CustomChatMessageRepository {

    ChatMessagePaginationResultDto findChatMessagesWithPagination(Long chatRoomId, Cursor cursor);

    Map<Long, ChatMessage> findChatRoomLastChatMessageMapping(List<Long> chatRoomIds);
}
