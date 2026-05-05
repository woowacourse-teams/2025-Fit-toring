package fittoring.application.community.dummy;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DummyPublishDao {

    private static final String SELECT_NEXT_POST_BASE = """
            SELECT id, title, content, nickname, guest_password, scheduled_at, attempt_count
            FROM dummy_post_pending
            WHERE status = 'PENDING' AND scheduled_at <= ?
            """;

    private static final String SELECT_NEXT_POST_ORDER = """
            ORDER BY scheduled_at, id
            LIMIT 1
            FOR UPDATE SKIP LOCKED
            """;

    private static final String SELECT_NEXT_COMMENT_BASE = """
            SELECT c.id, c.content, c.nickname, c.guest_password, c.scheduled_at, c.attempt_count,
                   p.published_post_id AS published_post_id,
                   r.published_comment_id AS published_root_id,
                   pr.published_comment_id AS published_parent_id
            FROM dummy_comment_pending c
            JOIN dummy_post_pending p ON c.pending_post_id = p.id
            LEFT JOIN dummy_comment_pending r ON c.pending_root_id = r.id
            LEFT JOIN dummy_comment_pending pr ON c.pending_parent_id = pr.id
            WHERE c.status = 'PENDING' AND c.scheduled_at <= ?
              AND p.status = 'PUBLISHED'
              AND p.published_post_id IS NOT NULL
              AND (c.pending_root_id IS NULL OR (r.status = 'PUBLISHED' AND r.published_comment_id IS NOT NULL))
              AND (c.pending_parent_id IS NULL OR (pr.status = 'PUBLISHED' AND pr.published_comment_id IS NOT NULL))
            """;

    private static final String SELECT_NEXT_COMMENT_ORDER = """
            ORDER BY c.scheduled_at, c.id
            LIMIT 1
            FOR UPDATE SKIP LOCKED
            """;

    private static final String INSERT_PUBLISHED_POST = """
            INSERT INTO post
                (title, content, member_id, nickname, guest_password, is_anonymous,
                 view_count, like_count, created_at, is_deleted)
            VALUES (?, ?, NULL, ?, ?, FALSE, 0, 0, ?, FALSE)
            """;

    private static final String INSERT_PUBLISHED_COMMENT = """
            INSERT INTO comment
                (content, post_id, member_id, nickname, guest_password, is_anonymous,
                 root_id, parent_id, created_at, is_deleted)
            VALUES (?, ?, NULL, ?, ?, FALSE, ?, ?, ?, FALSE)
            """;

    private static final String MARK_POST_PUBLISHED = """
            UPDATE dummy_post_pending
            SET published_post_id = ?, status = 'PUBLISHED'
            WHERE id = ?
            """;

    private static final String MARK_COMMENT_PUBLISHED = """
            UPDATE dummy_comment_pending
            SET published_comment_id = ?, status = 'PUBLISHED'
            WHERE id = ?
            """;

    private static final String MARK_POST_FAILED_ATTEMPT = """
            UPDATE dummy_post_pending
            SET attempt_count = attempt_count + 1,
                status = CASE WHEN attempt_count + 1 >= ? THEN 'FAILED' ELSE status END
            WHERE id = ?
            """;

    private static final String MARK_COMMENT_FAILED_ATTEMPT = """
            UPDATE dummy_comment_pending
            SET attempt_count = attempt_count + 1,
                status = CASE WHEN attempt_count + 1 >= ? THEN 'FAILED' ELSE status END
            WHERE id = ?
            """;

    private final JdbcTemplate jdbc;

    public Optional<PostPendingRow> findNextPostForPublish(Collection<Long> excludedIds, LocalDateTime publishableAt) {
        Query query = selectNextPostQuery(excludedIds, publishableAt);
        List<PostPendingRow> rows = jdbc.query(query.sql(), (rs, rowNum) -> new PostPendingRow(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getString("nickname"),
                rs.getString("guest_password"),
                rs.getObject("scheduled_at", LocalDateTime.class),
                rs.getInt("attempt_count")
        ), query.args());
        return rows.stream().findFirst();
    }

    public Optional<CommentPendingRow> findNextCommentForPublish(Collection<Long> excludedIds, LocalDateTime publishableAt) {
        Query query = selectNextCommentQuery(excludedIds, publishableAt);
        List<CommentPendingRow> rows = jdbc.query(query.sql(), (rs, rowNum) -> new CommentPendingRow(
                rs.getLong("id"),
                rs.getString("content"),
                rs.getString("nickname"),
                rs.getString("guest_password"),
                rs.getObject("scheduled_at", LocalDateTime.class),
                rs.getInt("attempt_count"),
                rs.getLong("published_post_id"),
                nullableLong(rs, "published_root_id"),
                nullableLong(rs, "published_parent_id")
        ), query.args());
        return rows.stream().findFirst();
    }

    public long insertPublishedPost(PostPendingRow row, LocalDateTime publishedAt) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_PUBLISHED_POST, new String[]{"id"});
            ps.setString(1, row.title());
            ps.setString(2, row.content());
            ps.setString(3, row.nickname());
            ps.setString(4, row.guestPassword());
            ps.setObject(5, publishedAt);
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public long insertPublishedComment(CommentPendingRow row, LocalDateTime publishedAt) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_PUBLISHED_COMMENT, new String[]{"id"});
            ps.setString(1, row.content());
            ps.setLong(2, row.publishedPostId());
            ps.setString(3, row.nickname());
            ps.setString(4, row.guestPassword());
            setNullableLong(ps, 5, row.publishedRootId());
            setNullableLong(ps, 6, row.publishedParentId());
            ps.setObject(7, publishedAt);
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public void markPostPublished(long pendingPostId, long publishedPostId) {
        jdbc.update(MARK_POST_PUBLISHED, publishedPostId, pendingPostId);
    }

    public void markCommentPublished(long pendingCommentId, long publishedCommentId) {
        jdbc.update(MARK_COMMENT_PUBLISHED, publishedCommentId, pendingCommentId);
    }

    public void markPostFailedAttempt(long pendingPostId, int maxAttempt) {
        jdbc.update(MARK_POST_FAILED_ATTEMPT, maxAttempt, pendingPostId);
    }

    public void markCommentFailedAttempt(long pendingCommentId, int maxAttempt) {
        jdbc.update(MARK_COMMENT_FAILED_ATTEMPT, maxAttempt, pendingCommentId);
    }

    private Long nullableLong(java.sql.ResultSet rs, String column) throws java.sql.SQLException {
        long value = rs.getLong(column);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

    private void setNullableLong(PreparedStatement ps, int index, Long value) throws java.sql.SQLException {
        if (value == null) {
            ps.setNull(index, Types.BIGINT);
            return;
        }
        ps.setLong(index, value);
    }

    private Query selectNextPostQuery(Collection<Long> excludedIds, LocalDateTime publishableAt) {
        if (excludedIds.isEmpty()) {
            return new Query(SELECT_NEXT_POST_BASE + SELECT_NEXT_POST_ORDER, new Object[]{publishableAt});
        }
        Object[] args = withPublishableAt(publishableAt, excludedIds);
        return new Query(
                SELECT_NEXT_POST_BASE + excludeIds("id", excludedIds.size()) + SELECT_NEXT_POST_ORDER,
                args
        );
    }

    private Query selectNextCommentQuery(Collection<Long> excludedIds, LocalDateTime publishableAt) {
        if (excludedIds.isEmpty()) {
            return new Query(SELECT_NEXT_COMMENT_BASE + SELECT_NEXT_COMMENT_ORDER, new Object[]{publishableAt});
        }
        Object[] args = withPublishableAt(publishableAt, excludedIds);
        return new Query(
                SELECT_NEXT_COMMENT_BASE + excludeIds("c.id", excludedIds.size()) + SELECT_NEXT_COMMENT_ORDER,
                args
        );
    }

    private Object[] withPublishableAt(LocalDateTime publishableAt, Collection<Long> excludedIds) {
        Object[] args = new Object[excludedIds.size() + 1];
        args[0] = publishableAt;
        int index = 1;
        for (Long excludedId : excludedIds) {
            args[index++] = excludedId;
        }
        return args;
    }

    private String excludeIds(String columnName, int size) {
        return " AND " + columnName + " NOT IN (" + String.join(", ", Collections.nCopies(size, "?")) + ")\n";
    }

    private record Query(String sql, Object[] args) {
    }

    public record PostPendingRow(
            long id,
            String title,
            String content,
            String nickname,
            String guestPassword,
            LocalDateTime scheduledAt,
            int attemptCount
    ) {
    }

    public record CommentPendingRow(
            long id,
            String content,
            String nickname,
            String guestPassword,
            LocalDateTime scheduledAt,
            int attemptCount,
            long publishedPostId,
            Long publishedRootId,
            Long publishedParentId
    ) {
    }
}
