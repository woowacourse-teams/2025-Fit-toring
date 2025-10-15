package fittoring.mentoring.business.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fittoring.mentoring.Cursor;
import fittoring.mentoring.business.model.ChatMessage;
import fittoring.mentoring.business.model.QChatMessage;
import fittoring.mentoring.business.model.SortKey;
import fittoring.mentoring.business.service.dto.chat.ChatMessagePaginationResult;
import fittoring.util.CursorCodec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomChatMessageRepositoryImpl implements CustomChatMessageRepository {

    private static final int PAGE_SIZE = 20;
    private static final QChatMessage chatMessage = QChatMessage.chatMessage;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public ChatMessagePaginationResult findChatMessagesWithPagination(Long chatRoomId, SortKey sortKey, Cursor cursor) {

        BooleanBuilder where = new BooleanBuilder();
        BooleanExpression cursorCondition = buildCursorCondition(sortKey, cursor);

        where.and(chatMessage.chatRoomId.eq(chatRoomId))
                .and(cursorCondition);

        List<ChatMessage> rows = jpaQueryFactory.selectFrom(chatMessage)
                .where(where)
                .orderBy(orderSpecifiers(sortKey))
                .limit(PAGE_SIZE + 1L)
                .fetch();

        boolean hasNext = rows.size() > PAGE_SIZE;
        String nextCursorCode = null;
        if (hasNext) {
            ChatMessage nextChatMessage = rows.getLast();
            rows = rows.subList(0, PAGE_SIZE);
            nextCursorCode = switch (sortKey) {
                case CREATED_AT -> getNextCursorCode(nextChatMessage);
                // 다른 정렬 기준이 추가될 수 있습니다.
            };
        }
        return new ChatMessagePaginationResult(rows, nextCursorCode, hasNext);
    }

    private String getNextCursorCode(ChatMessage nextChatMessage) {
        String nextCursorCode;
        long nextSortValue = nextChatMessage.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond();
        nextCursorCode = CursorCodec.encode(new Cursor(nextSortValue, nextChatMessage.getId()));
        return nextCursorCode;
    }

    private BooleanExpression buildCursorCondition(SortKey sortKey, Cursor cursor) {
        if (sortKey.equals(SortKey.CREATED_AT) && cursor != null) {
            LocalDateTime cursorDateTime = Instant.ofEpochSecond(cursor.sortValue())
                    .atZone(ZoneId.of("Asia/Seoul"))
                    .toLocalDateTime();
            return chatMessage.createdAt.lt(cursorDateTime)
                    .or(
                            chatMessage.createdAt.eq(cursorDateTime)
                                    .and(chatMessage.id.loe(cursor.id()))
                    );
        }
        return null;
    }

    private OrderSpecifier<?>[] orderSpecifiers(SortKey sortKey) {
        return switch (sortKey) {
            case CREATED_AT -> new OrderSpecifier<?>[]{
                    chatMessage.createdAt.desc(),
                    chatMessage.id.desc()
            };
        };
    }
}
