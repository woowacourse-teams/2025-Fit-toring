package fittoring.application.mentoring.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fittoring.application.chatroom.service.dto.ChatMessagePaginationResultDto;
import fittoring.domain.model.ChatMessage;
import fittoring.domain.model.QChatMessage;
import fittoring.util.Cursor;
import fittoring.util.CursorCodec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomChatMessageRepositoryImpl implements CustomChatMessageRepository {

    private static final int PAGE_SIZE = 20;
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final QChatMessage chatMessage = QChatMessage.chatMessage;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public ChatMessagePaginationResultDto findChatMessagesWithPagination(Long chatRoomId, Cursor cursor) {
        BooleanBuilder where = buildWhereCondition(chatRoomId, cursor);

        List<ChatMessage> rows = jpaQueryFactory.selectFrom(chatMessage)
                .where(where)
                .orderBy(orderSpecifiers())
                .limit(PAGE_SIZE + 1L)
                .fetch();

        boolean hasNext = rows.size() > PAGE_SIZE;
        String nextCursorCode = null;
        if (hasNext) {
            ChatMessage nextChatMessage = rows.getLast();
            rows = rows.subList(0, PAGE_SIZE);
            nextCursorCode = getNextCursorCode(nextChatMessage);
        }
        return new ChatMessagePaginationResultDto(rows, nextCursorCode, hasNext);
    }

    private BooleanBuilder buildWhereCondition(Long chatRoomId, Cursor cursor) {
        BooleanBuilder where = new BooleanBuilder();
        BooleanExpression cursorCondition = buildCursorCondition(cursor);

        where.and(chatMessage.chatRoomId.eq(chatRoomId))
                .and(cursorCondition);
        return where;
    }

    private BooleanExpression buildCursorCondition(Cursor cursor) {
        if (cursor != null) {
            LocalDateTime cursorDateTime = Instant.ofEpochSecond(cursor.sortValue())
                    .atZone(DEFAULT_ZONE_ID)
                    .toLocalDateTime();
            return chatMessage.createdAt.lt(cursorDateTime)
                    .or(
                            chatMessage.createdAt.eq(cursorDateTime)
                                    .and(chatMessage.id.loe(cursor.id()))
                    );
        }
        return null;
    }

    private String getNextCursorCode(ChatMessage nextChatMessage) {
        long nextSortValue = nextChatMessage.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond();
        return CursorCodec.encode(new Cursor(nextSortValue, nextChatMessage.getId()));
    }

    private OrderSpecifier<?>[] orderSpecifiers() {
        return new OrderSpecifier<?>[]{
                chatMessage.createdAt.desc(),
                chatMessage.id.desc()
        };
    }
}
