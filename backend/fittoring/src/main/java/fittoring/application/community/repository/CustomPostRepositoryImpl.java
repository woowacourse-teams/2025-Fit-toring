package fittoring.application.community.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fittoring.application.community.service.dto.PostPaginationResult;
import fittoring.domain.model.Post;
import fittoring.domain.model.QPost;
import fittoring.util.Cursor;
import fittoring.util.CursorCodec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private static final int PAGE_SIZE = 10;
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final QPost post = QPost.post;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public PostPaginationResult findPostsWithPagination(Cursor cursor) {
        BooleanBuilder where = new BooleanBuilder();
        BooleanExpression cursorCondition = buildCursorCondition(cursor);
        if (cursorCondition != null) {
            where.and(cursorCondition);
        }

        List<Post> rows = jpaQueryFactory.selectFrom(post)
                .where(where)
                .orderBy(orderSpecifiers())
                .limit(PAGE_SIZE + 1L)
                .fetch();

        boolean hasNext = rows.size() > PAGE_SIZE;
        String nextCursorCode = null;
        if (hasNext) {
            Post nextPost = rows.getLast();
            rows = rows.subList(0, PAGE_SIZE);
            long nextSortValue = nextPost.getCreatedAt().atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
            nextCursorCode = CursorCodec.encode(new Cursor(nextSortValue, nextPost.getId()));
        }
        return new PostPaginationResult(rows, nextCursorCode, hasNext);
    }

    private BooleanExpression buildCursorCondition(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        LocalDateTime cursorDateTime = Instant.ofEpochMilli(cursor.sortValue())
                .atZone(DEFAULT_ZONE_ID)
                .toLocalDateTime();
        return post.createdAt.lt(cursorDateTime)
                .or(post.createdAt.eq(cursorDateTime).and(post.id.loe(cursor.id())));
    }

    private OrderSpecifier<?>[] orderSpecifiers() {
        return new OrderSpecifier<?>[]{
                post.createdAt.desc(),
                post.id.desc()
        };
    }
}
