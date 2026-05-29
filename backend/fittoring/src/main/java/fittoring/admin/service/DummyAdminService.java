package fittoring.admin.service;

import fittoring.admin.config.DummyAdminApiProperties;
import fittoring.admin.exception.DummyAlreadyInsertedException;
import fittoring.admin.exception.InvalidDummyScenarioException;
import fittoring.admin.presentation.dto.CommentPreview;
import fittoring.admin.presentation.dto.DummyScenarioPreviewResponse;
import fittoring.admin.presentation.dto.DummySqlInsertResponse;
import fittoring.admin.presentation.dto.DummySqlInsertStatusResponse;
import fittoring.admin.presentation.dto.PostPreview;
import fittoring.admin.repository.DummyPendingDao;
import fittoring.admin.repository.DummyPendingDao.WriteResult;
import fittoring.admin.repository.DummyScenarioDao;
import fittoring.admin.repository.DummyScenarioRow;
import fittoring.admin.repository.DummyScenarioStatus;
import fittoring.admin.repository.DummyScenarioSummaryRow;
import fittoring.application.community.dummy.scenario.Scenario;
import fittoring.application.community.dummy.scenario.ScenarioComment;
import fittoring.application.community.dummy.scenario.ScenarioFile;
import fittoring.application.community.dummy.scenario.ScenarioLoader;
import fittoring.application.community.dummy.scenario.ScenarioPost;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DummyAdminService {

    private static final String STATUS_INSERTED = "INSERTED";
    private static final ZoneId SCHEDULE_ZONE = ZoneId.of("Asia/Seoul");

    private final DummyPendingDao pendingDao;
    private final DummyScenarioDao scenarioDao;
    private final ScenarioLoader scenarioLoader;
    private final DummyAdminApiProperties properties;

    public List<DummySqlInsertStatusResponse> list() {
        return scenarioDao.findAll().stream()
                .map(this::toStatusResponse)
                .toList();
    }

    public DummySqlInsertStatusResponse status(long scenarioId) {
        return toStatusResponse(scenarioDao.getById(scenarioId));
    }

    public DummyScenarioPreviewResponse preview(long scenarioId) {
        DummyScenarioRow scenario = scenarioDao.getById(scenarioId);
        ScenarioFile parsed = parse(scenario.yamlContent(), scenario.originalFilename());
        return toPreviewResponse(scenario, parsed);
    }

    @Transactional
    public DummySqlInsertStatusResponse upload(MultipartFile file) {
        validateUploadExtension(file);
        String yamlContent = readUploadedContent(file);
        ScenarioFile parsed = parse(yamlContent, file.getOriginalFilename());
        OffsetDateTime originalStartAt = findEarliestScheduledAt(parsed);
        Duration originalDuration = calculateOriginalDuration(parsed);
        long scenarioId = scenarioDao.save(
                file.getOriginalFilename(),
                sha256(yamlContent),
                yamlContent,
                OffsetDateTime.now(SCHEDULE_ZONE),
                originalStartAt,
                originalDuration,
                parsed.scenarios().size(),
                countComments(parsed)
        );
        return toStatusResponse(scenarioDao.getById(scenarioId));
    }

    private void validateUploadExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new InvalidDummyScenarioException("업로드된 파일명이 비어 있습니다");
        }
        String lower = originalFilename.toLowerCase();
        if (!lower.endsWith(".yml") && !lower.endsWith(".yaml")) {
            throw new InvalidDummyScenarioException("YAML 파일(.yml/.yaml)만 업로드할 수 있습니다: " + originalFilename);
        }
    }

    private String readUploadedContent(MultipartFile file) {
        try {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("업로드된 파일을 읽지 못했습니다", e);
        }
    }

    @Transactional
    public DummySqlInsertResponse insert(long scenarioId, OffsetDateTime startAt, Duration duration) {
        DummyScenarioRow scenario = scenarioDao.getById(scenarioId);
        if (scenario.status() == DummyScenarioStatus.INSERTED || pendingDao.existsByScenarioId(scenarioId)) {
            throw new DummyAlreadyInsertedException("scenarioId=" + scenarioId);
        }
        ScenarioFile originalScenario = parse(scenario.yamlContent(), scenario.originalFilename());
        ScenarioFile appliedScenario = applyScheduleOptions(originalScenario, startAt, duration);
        Duration appliedDuration = calculateOriginalDuration(appliedScenario);
        OffsetDateTime appliedStartAt = findEarliestScheduledAt(appliedScenario);
        WriteResult result = pendingDao.insertAll(scenarioId, appliedScenario, properties.getGuestPasswordHash());
        scenarioDao.markInserted(scenarioId, OffsetDateTime.now(SCHEDULE_ZONE), appliedStartAt, appliedDuration);
        return new DummySqlInsertResponse(
                scenarioId,
                scenario.originalFilename(),
                appliedScenario.scenarios().size(),
                result.postCount(),
                result.commentCount(),
                STATUS_INSERTED,
                appliedStartAt,
                appliedDuration
        );
    }

    private ScenarioFile applyScheduleOptions(ScenarioFile originalScenario, OffsetDateTime startAt, Duration duration) {
        ScenarioFile durationApplied = applyDuration(originalScenario, duration);
        ScenarioFile startAtApplied = applyStartAt(durationApplied, startAt);
        return applyScheduleOffset(startAtApplied);
    }

    private ScenarioFile parse(String yamlContent, String scenarioName) {
        try (ByteArrayInputStream input = new ByteArrayInputStream(yamlContent.getBytes(StandardCharsets.UTF_8))) {
            return scenarioLoader.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("시나리오 파일을 읽지 못했습니다: " + scenarioName, e);
        } catch (RuntimeException e) {
            throw new InvalidDummyScenarioException("유효하지 않은 시나리오 파일입니다: " + scenarioName, e);
        }
    }

    private DummySqlInsertStatusResponse toStatusResponse(DummyScenarioRow scenario) {
        return new DummySqlInsertStatusResponse(
                scenario.id(),
                scenario.originalFilename(),
                scenario.status().name(),
                scenario.uploadedAt(),
                scenario.insertedAt(),
                scenario.appliedStartAt(),
                scenario.originalDuration(),
                scenario.appliedDuration(),
                scenario.postCount(),
                scenario.commentCount()
        );
    }

    private DummySqlInsertStatusResponse toStatusResponse(DummyScenarioSummaryRow scenario) {
        return new DummySqlInsertStatusResponse(
                scenario.id(),
                scenario.originalFilename(),
                scenario.status().name(),
                scenario.uploadedAt(),
                scenario.insertedAt(),
                scenario.appliedStartAt(),
                scenario.originalDuration(),
                scenario.appliedDuration(),
                scenario.postCount(),
                scenario.commentCount()
        );
    }

    private DummyScenarioPreviewResponse toPreviewResponse(DummyScenarioRow scenario, ScenarioFile file) {
        List<PostPreview> posts = file.scenarios().stream()
                .map(this::toPostPreview)
                .toList();
        return new DummyScenarioPreviewResponse(
                scenario.id(),
                scenario.originalFilename(),
                calculateOriginalDuration(file),
                posts
        );
    }

    private PostPreview toPostPreview(Scenario scenario) {
        ScenarioPost post = scenario.post();
        return new PostPreview(
                post.nickname(),
                post.scheduledAt(),
                post.title(),
                post.content(),
                toCommentPreviews(scenario.comments())
        );
    }

    private List<CommentPreview> toCommentPreviews(List<ScenarioComment> comments) {
        return comments.stream()
                .map(this::toCommentPreview)
                .toList();
    }

    private CommentPreview toCommentPreview(ScenarioComment comment) {
        return new CommentPreview(
                comment.nickname(),
                comment.scheduledAt(),
                comment.content(),
                toCommentPreviews(comment.replies())
        );
    }

    ScenarioFile applyDuration(ScenarioFile file, Duration newDuration) {
        if (newDuration == null) {
            return file;
        }
        if (newDuration.isZero() || newDuration.isNegative()) {
            throw new InvalidDummyScenarioException("duration은 0보다 커야 합니다: " + newDuration);
        }
        Duration originalDuration = calculateOriginalDuration(file);
        if (originalDuration.isZero()) {
            throw new InvalidDummyScenarioException("원본 duration이 0인 시나리오는 duration을 변경할 수 없습니다");
        }
        OffsetDateTime originalStartAt = findEarliestScheduledAt(file);
        List<Scenario> scenarios = file.scenarios().stream()
                .map(scenario -> new Scenario(
                        scalePost(scenario.post(), originalStartAt, originalDuration, newDuration),
                        scaleComments(scenario.comments(), originalStartAt, originalDuration, newDuration)
                ))
                .toList();
        return new ScenarioFile(scenarios);
    }

    private ScenarioPost scalePost(
            ScenarioPost post,
            OffsetDateTime originalStartAt,
            Duration originalDuration,
            Duration newDuration
    ) {
        return new ScenarioPost(
                post.nickname(),
                scaleScheduledAt(post.scheduledAt(), originalStartAt, originalDuration, newDuration),
                post.title(),
                post.content()
        );
    }

    private List<ScenarioComment> scaleComments(
            List<ScenarioComment> comments,
            OffsetDateTime originalStartAt,
            Duration originalDuration,
            Duration newDuration
    ) {
        return comments.stream()
                .map(comment -> new ScenarioComment(
                        comment.nickname(),
                        scaleScheduledAt(comment.scheduledAt(), originalStartAt, originalDuration, newDuration),
                        comment.content(),
                        scaleComments(comment.replies(), originalStartAt, originalDuration, newDuration)
                ))
                .toList();
    }

    private OffsetDateTime scaleScheduledAt(
            OffsetDateTime scheduledAt,
            OffsetDateTime originalStartAt,
            Duration originalDuration,
            Duration newDuration
    ) {
        Duration originalOffset = Duration.between(originalStartAt, scheduledAt);
        Duration scaledOffset = scaleOffset(originalOffset, originalDuration, newDuration);
        return originalStartAt.plus(scaledOffset);
    }

    private Duration scaleOffset(Duration originalOffset, Duration originalDuration, Duration newDuration) {
        if (originalOffset.isZero()) {
            return Duration.ZERO;
        }
        try {
            BigInteger offsetNanos = BigInteger.valueOf(originalOffset.toNanos());
            BigInteger newDurationNanos = BigInteger.valueOf(newDuration.toNanos());
            BigInteger originalDurationNanos = BigInteger.valueOf(originalDuration.toNanos());
            BigInteger scaledNanos = offsetNanos.multiply(newDurationNanos).divide(originalDurationNanos);
            return Duration.ofNanos(scaledNanos.longValueExact());
        } catch (ArithmeticException e) {
            throw new InvalidDummyScenarioException("duration 범위를 초과했습니다: " + newDuration, e);
        }
    }

    private ScenarioFile applyStartAt(ScenarioFile file, OffsetDateTime startAt) {
        if (startAt == null) {
            return file;
        }
        OffsetDateTime originalStartAt = findEarliestScheduledAt(file);
        return shift(file, Duration.between(originalStartAt, startAt));
    }

    private ScenarioFile applyScheduleOffset(ScenarioFile file) {
        int offsetDays = properties.getScheduleOffsetDays();
        if (offsetDays < 0) {
            throw new InvalidDummyScenarioException("schedule-offset-days는 0 이상이어야 합니다: " + offsetDays);
        }
        if (offsetDays == 0) {
            return file;
        }
        return shift(file, Duration.ofDays(offsetDays));
    }

    private ScenarioFile shift(ScenarioFile file, Duration duration) {
        List<Scenario> scenarios = file.scenarios().stream()
                .map(scenario -> new Scenario(
                        shiftPost(scenario.post(), duration),
                        shiftComments(scenario.comments(), duration)
                ))
                .toList();
        return new ScenarioFile(scenarios);
    }

    private ScenarioPost shiftPost(ScenarioPost post, Duration duration) {
        return new ScenarioPost(
                post.nickname(),
                post.scheduledAt().plus(duration),
                post.title(),
                post.content()
        );
    }

    private List<ScenarioComment> shiftComments(List<ScenarioComment> comments, Duration duration) {
        return comments.stream()
                .map(comment -> new ScenarioComment(
                        comment.nickname(),
                        comment.scheduledAt().plus(duration),
                        comment.content(),
                        shiftComments(comment.replies(), duration)
                ))
                .toList();
    }

    private OffsetDateTime findEarliestScheduledAt(ScenarioFile file) {
        return file.scenarios().stream()
                .flatMap(this::scheduledTimes)
                .min(OffsetDateTime::compareTo)
                .orElseThrow(() -> new InvalidDummyScenarioException("시나리오가 비어 있습니다"));
    }

    private Duration calculateOriginalDuration(ScenarioFile file) {
        OffsetDateTime earliest = findEarliestScheduledAt(file);
        OffsetDateTime latest = file.scenarios().stream()
                .flatMap(this::scheduledTimes)
                .max(OffsetDateTime::compareTo)
                .orElseThrow(() -> new InvalidDummyScenarioException("시나리오가 비어 있습니다"));
        return Duration.between(earliest, latest);
    }

    private Stream<OffsetDateTime> scheduledTimes(Scenario scenario) {
        return Stream.concat(
                Stream.of(scenario.post().scheduledAt()),
                commentScheduledTimes(scenario.comments())
        );
    }

    private Stream<OffsetDateTime> commentScheduledTimes(List<ScenarioComment> comments) {
        return comments.stream()
                .flatMap(comment -> Stream.concat(
                        Stream.of(comment.scheduledAt()),
                        commentScheduledTimes(comment.replies())
                ));
    }

    private int countComments(ScenarioFile file) {
        return file.scenarios().stream()
                .mapToInt(scenario -> countComments(scenario.comments()))
                .sum();
    }

    private int countComments(List<ScenarioComment> comments) {
        return comments.stream()
                .mapToInt(comment -> 1 + countComments(comment.replies()))
                .sum();
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 해시 알고리즘을 사용할 수 없습니다", e);
        }
    }
}
