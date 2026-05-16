package fittoring.admin.repository;

import fittoring.admin.exception.DummyAlreadyInsertedException;
import fittoring.application.community.dummy.scenario.Scenario;
import fittoring.application.community.dummy.scenario.ScenarioComment;
import fittoring.application.community.dummy.scenario.ScenarioFile;
import fittoring.application.community.dummy.scenario.ScenarioPost;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DummyPendingDao {

    private static final String EXISTS_BY_SCENARIO_FILE = """
            SELECT EXISTS (
                SELECT 1 FROM dummy_post_pending WHERE scenario_file = ?
            )
            """;

    private static final String FIND_EARLIEST_SCHEDULED_AT = """
            SELECT MIN(scheduled_at) FROM dummy_post_pending WHERE scenario_file = ?
            """;

    private static final ZoneOffset KST = ZoneOffset.ofHours(9);

    private static final String INSERT_POST = """
            INSERT INTO dummy_post_pending
                (scenario_file, scenario_seq, title, content, nickname, guest_password,
                 scheduled_at, status, attempt_count)
            VALUES (?, ?, ?, ?, ?, ?, ?, 'PENDING', 0)
            """;

    private static final String INSERT_COMMENT = """
            INSERT INTO dummy_comment_pending
                (pending_post_id, pending_root_id, pending_parent_id, content, nickname,
                 guest_password, scheduled_at, status, attempt_count)
            VALUES (?, ?, ?, ?, ?, ?, ?, 'PENDING', 0)
            """;

    private final JdbcTemplate jdbc;

    public boolean existsByScenarioFile(String scenarioFile) {
        Boolean exists = jdbc.queryForObject(EXISTS_BY_SCENARIO_FILE, Boolean.class, scenarioFile);
        return Boolean.TRUE.equals(exists);
    }

    public Optional<OffsetDateTime> findEarliestScheduledAt(String scenarioFile) {
        Timestamp earliest = jdbc.queryForObject(FIND_EARLIEST_SCHEDULED_AT, Timestamp.class, scenarioFile);
        if (earliest == null) {
            return Optional.empty();
        }
        return Optional.of(earliest.toLocalDateTime().atOffset(KST));
    }

    @Transactional
    public WriteResult insertAll(String scenarioFile, ScenarioFile file, String guestPasswordHash) {
        try {
            int seq = 1;
            int commentCount = 0;
            for (Scenario scenario : file.scenarios()) {
                long postId = insertPost(scenarioFile, seq, scenario.post(), guestPasswordHash);
                for (ScenarioComment root : scenario.comments()) {
                    commentCount += insertCommentTree(root, postId, null, null, guestPasswordHash);
                }
                seq++;
            }
            return new WriteResult(file.scenarios().size(), commentCount);
        } catch (DataIntegrityViolationException e) {
            throw new DummyAlreadyInsertedException(scenarioFile, e);
        }
    }

    private long insertPost(String scenarioFile, int seq, ScenarioPost post, String guestPasswordHash) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_POST, new String[]{"id"});
            ps.setString(1, scenarioFile);
            ps.setInt(2, seq);
            ps.setString(3, post.title());
            ps.setString(4, post.content());
            ps.setString(5, post.nickname());
            ps.setString(6, guestPasswordHash);
            ps.setTimestamp(7, toTimestamp(post.scheduledAt()));
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private int insertCommentTree(ScenarioComment comment,
                                  long postId,
                                  Long rootId,
                                  Long parentId,
                                  String guestPasswordHash) {
        long commentId = insertComment(comment, postId, rootId, parentId, guestPasswordHash);
        long resolvedRoot = (rootId == null) ? commentId : rootId;
        int count = 1;
        for (ScenarioComment reply : comment.replies()) {
            count += insertCommentTree(reply, postId, resolvedRoot, commentId, guestPasswordHash);
        }
        return count;
    }

    private long insertComment(ScenarioComment comment,
                               long postId,
                               Long rootId,
                               Long parentId,
                               String guestPasswordHash) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_COMMENT, new String[]{"id"});
            ps.setLong(1, postId);
            if (rootId == null) {
                ps.setNull(2, Types.BIGINT);
            } else {
                ps.setLong(2, rootId);
            }
            if (parentId == null) {
                ps.setNull(3, Types.BIGINT);
            } else {
                ps.setLong(3, parentId);
            }
            ps.setString(4, comment.content());
            ps.setString(5, comment.nickname());
            ps.setString(6, guestPasswordHash);
            ps.setTimestamp(7, toTimestamp(comment.scheduledAt()));
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private Timestamp toTimestamp(OffsetDateTime at) {
        return Timestamp.valueOf(at.toLocalDateTime());
    }

    public record WriteResult(int postCount, int commentCount) {
    }
}
