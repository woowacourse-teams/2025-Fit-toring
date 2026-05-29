package fittoring.admin.repository;

import fittoring.admin.exception.DummyScenarioNotFoundException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
public class DummyScenarioDao {

    private static final ZoneOffset KST = ZoneOffset.ofHours(9);

    private static final String INSERT = """
            INSERT INTO dummy_scenario
                (original_filename, content_hash, yaml_content, status, uploaded_at,
                 original_start_at, original_duration_seconds, post_count, comment_count)
            VALUES (?, ?, ?, 'UPLOADED', ?, ?, ?, ?, ?)
            """;

    private static final String FIND_ALL = """
            SELECT id, original_filename, content_hash, status, uploaded_at, inserted_at,
                   original_start_at, original_duration_seconds, applied_start_at,
                   applied_duration_seconds, post_count, comment_count
            FROM dummy_scenario
            ORDER BY id DESC
            """;

    private static final String FIND_BY_ID = """
            SELECT id, original_filename, content_hash, yaml_content, status, uploaded_at, inserted_at,
                   original_start_at, original_duration_seconds, applied_start_at,
                   applied_duration_seconds, post_count, comment_count
            FROM dummy_scenario
            WHERE id = ?
            """;

    private static final String MARK_INSERTED = """
            UPDATE dummy_scenario
            SET status = 'INSERTED',
                inserted_at = ?,
                applied_start_at = ?,
                applied_duration_seconds = ?
            WHERE id = ?
            """;

    private final JdbcTemplate jdbc;

    public long save(
            String originalFilename,
            String contentHash,
            String yamlContent,
            OffsetDateTime uploadedAt,
            OffsetDateTime originalStartAt,
            Duration originalDuration,
            int postCount,
            int commentCount
    ) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT, new String[]{"id"});
            ps.setString(1, originalFilename);
            ps.setString(2, contentHash);
            ps.setString(3, yamlContent);
            ps.setTimestamp(4, toTimestamp(uploadedAt));
            ps.setTimestamp(5, toTimestamp(originalStartAt));
            ps.setLong(6, originalDuration.toSeconds());
            ps.setInt(7, postCount);
            ps.setInt(8, commentCount);
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public List<DummyScenarioSummaryRow> findAll() {
        return jdbc.query(FIND_ALL, (rs, rowNum) -> new DummyScenarioSummaryRow(
                rs.getLong("id"),
                rs.getString("original_filename"),
                rs.getString("content_hash"),
                DummyScenarioStatus.valueOf(rs.getString("status")),
                toOffsetDateTime(rs.getTimestamp("uploaded_at")),
                toNullableOffsetDateTime(rs.getTimestamp("inserted_at")),
                toOffsetDateTime(rs.getTimestamp("original_start_at")),
                Duration.ofSeconds(rs.getLong("original_duration_seconds")),
                toNullableOffsetDateTime(rs.getTimestamp("applied_start_at")),
                toNullableDuration(rs.getObject("applied_duration_seconds", Long.class)),
                rs.getInt("post_count"),
                rs.getInt("comment_count")
        ));
    }

    public Optional<DummyScenarioRow> findById(long id) {
        List<DummyScenarioRow> rows = jdbc.query(FIND_BY_ID, (rs, rowNum) -> new DummyScenarioRow(
                rs.getLong("id"),
                rs.getString("original_filename"),
                rs.getString("content_hash"),
                rs.getString("yaml_content"),
                DummyScenarioStatus.valueOf(rs.getString("status")),
                toOffsetDateTime(rs.getTimestamp("uploaded_at")),
                toNullableOffsetDateTime(rs.getTimestamp("inserted_at")),
                toOffsetDateTime(rs.getTimestamp("original_start_at")),
                Duration.ofSeconds(rs.getLong("original_duration_seconds")),
                toNullableOffsetDateTime(rs.getTimestamp("applied_start_at")),
                toNullableDuration(rs.getObject("applied_duration_seconds", Long.class)),
                rs.getInt("post_count"),
                rs.getInt("comment_count")
        ), id);
        return rows.stream().findFirst();
    }

    public DummyScenarioRow getById(long id) {
        return findById(id).orElseThrow(() -> new DummyScenarioNotFoundException(id));
    }

    public void markInserted(long id, OffsetDateTime insertedAt, OffsetDateTime appliedStartAt, Duration appliedDuration) {
        jdbc.update(MARK_INSERTED, toTimestamp(insertedAt), toTimestamp(appliedStartAt),
                appliedDuration.toSeconds(), id);
    }

    private Timestamp toTimestamp(OffsetDateTime at) {
        return Timestamp.valueOf(at.toLocalDateTime());
    }

    private OffsetDateTime toOffsetDateTime(Timestamp timestamp) {
        return timestamp.toLocalDateTime().atOffset(KST);
    }

    private OffsetDateTime toNullableOffsetDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return toOffsetDateTime(timestamp);
    }

    private Duration toNullableDuration(Long seconds) {
        if (seconds == null) {
            return null;
        }
        return Duration.ofSeconds(seconds);
    }
}
