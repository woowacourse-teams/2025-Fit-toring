package fittoring.admin.service;

import fittoring.admin.config.DummyAdminApiProperties;
import fittoring.admin.exception.DummyAlreadyInsertedException;
import fittoring.admin.exception.DummyScenarioFileAlreadyExistsException;
import fittoring.admin.exception.DummyScenarioFileNotFoundException;
import fittoring.admin.exception.InvalidDummyScenarioException;
import fittoring.admin.presentation.dto.CommentPreview;
import fittoring.admin.presentation.dto.DummyScenarioPreviewResponse;
import fittoring.admin.presentation.dto.DummySqlInsertResponse;
import fittoring.admin.presentation.dto.DummySqlInsertStatusResponse;
import fittoring.admin.presentation.dto.PostPreview;
import fittoring.admin.repository.DummyPendingDao;
import fittoring.admin.repository.DummyPendingDao.WriteResult;
import fittoring.application.community.dummy.scenario.Scenario;
import fittoring.application.community.dummy.scenario.ScenarioComment;
import fittoring.application.community.dummy.scenario.ScenarioFile;
import fittoring.application.community.dummy.scenario.ScenarioLoader;
import fittoring.application.community.dummy.scenario.ScenarioPost;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DummyAdminService {

    private static final String SCENARIO_FILE_PREFIX = "scenarios";
    private static final String SCENARIO_FILE_SUFFIX = ".yml";
    private static final String CLASSPATH_PREFIX = "classpath:";
    private static final String CLASSPATH_ALL_PREFIX = "classpath*:";
    private static final String FILE_PREFIX = "file:";
    private static final Pattern SCENARIO_FILE_PATTERN = Pattern.compile("^scenarios(\\d+)\\.yml$");
    private static final String STATUS_INSERTED = "INSERTED";

    private final DummyPendingDao dao;
    private final ResourcePatternResolver resourceResolver;
    private final ScenarioLoader scenarioLoader;
    private final DummyAdminApiProperties properties;

    public List<DummySqlInsertStatusResponse> list() {
        ensureUploadDirectory();
        try {
            Stream<Resource> builtIns = Arrays.stream(resourceResolver.getResources(builtInPattern()));
            Stream<Resource> uploads = Arrays.stream(resourceResolver.getResources(uploadPattern()));
            return Stream.concat(builtIns, uploads)
                    .map(Resource::getFilename)
                    .filter(fileName -> fileName != null && SCENARIO_FILE_PATTERN.matcher(fileName).matches())
                    .distinct()
                    .map(this::toStatusResponse)
                    .sorted(Comparator.comparingInt(DummySqlInsertStatusResponse::fileSeq))
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException("시나리오 파일 목록을 읽지 못했습니다", e);
        }
    }

    public DummySqlInsertStatusResponse status(int fileSeq) {
        validateFileSeq(fileSeq);
        String scenarioFile = SCENARIO_FILE_PREFIX + fileSeq + SCENARIO_FILE_SUFFIX;
        return buildStatusResponse(fileSeq, scenarioFile);
    }

    public DummyScenarioPreviewResponse preview(int fileSeq) {
        validateFileSeq(fileSeq);
        String scenarioFile = SCENARIO_FILE_PREFIX + fileSeq + SCENARIO_FILE_SUFFIX;
        ScenarioFile parsed = parse(scenarioFile);
        return toPreviewResponse(fileSeq, scenarioFile, parsed);
    }

    public DummySqlInsertStatusResponse upload(MultipartFile file) {
        validateUploadExtension(file);
        ScenarioFile parsed = parseUploadedContent(file);
        int fileSeq = nextFileSeq();
        String scenarioFile = SCENARIO_FILE_PREFIX + fileSeq + SCENARIO_FILE_SUFFIX;
        saveUploadedFile(scenarioFile, file);
        return new DummySqlInsertStatusResponse(
                fileSeq,
                scenarioFile,
                false,
                null,
                calculateOriginalDuration(parsed)
        );
    }

    private void validateUploadExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new InvalidDummyScenarioException("업로드된 파일명이 비어 있습니다");
        }
        String lower = originalFilename.toLowerCase();
        if (!lower.endsWith(".yml") && !lower.endsWith(".yaml")) {
            throw new InvalidDummyScenarioException("YAML 파일(.yml/.yaml)만 업로드할 수 있습니다: " + originalFilename);
        }
    }

    private ScenarioFile parseUploadedContent(MultipartFile file) {
        try (InputStream input = file.getInputStream()) {
            return scenarioLoader.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("업로드된 파일을 읽지 못했습니다", e);
        } catch (RuntimeException e) {
            throw new InvalidDummyScenarioException("유효하지 않은 시나리오 파일입니다", e);
        }
    }

    private int nextFileSeq() {
        try {
            return Stream.concat(
                            Arrays.stream(resourceResolver.getResources(builtInPattern())),
                            Arrays.stream(resourceResolver.getResources(uploadPattern()))
                    )
                    .map(Resource::getFilename)
                    .filter(Objects::nonNull)
                    .map(SCENARIO_FILE_PATTERN::matcher)
                    .filter(Matcher::matches)
                    .mapToInt(matcher -> Integer.parseInt(matcher.group(1)))
                    .max()
                    .orElse(0) + 1;
        } catch (IOException e) {
            throw new IllegalStateException("다음 fileSeq 계산에 실패했습니다", e);
        }
    }

    private void saveUploadedFile(String scenarioFile, MultipartFile file) {
        ensureUploadDirectory();
        Path target = uploadFilesystemPath().resolve(scenarioFile);
        try (InputStream input = file.getInputStream()) {
            // 옵션 없이 호출하면 target이 이미 존재할 때 FileAlreadyExistsException 발생 (atomic).
            Files.copy(input, target);
        } catch (FileAlreadyExistsException e) {
            throw new DummyScenarioFileAlreadyExistsException(scenarioFile, e);
        } catch (IOException e) {
            throw new IllegalStateException("파일 저장에 실패했습니다: " + target, e);
        }
    }

    public DummySqlInsertResponse insert(int fileSeq) {
        return insert(fileSeq, null, null);
    }

    public DummySqlInsertResponse insert(int fileSeq, OffsetDateTime startAt) {
        return insert(fileSeq, startAt, null);
    }

    public DummySqlInsertResponse insert(int fileSeq, OffsetDateTime startAt, Duration duration) {
        validateFileSeq(fileSeq);
        String scenarioFile = SCENARIO_FILE_PREFIX + fileSeq + SCENARIO_FILE_SUFFIX;
        ScenarioFile parsed = parse(scenarioFile);
        parsed = applyDuration(parsed, duration);
        parsed = applyStartAt(parsed, startAt);
        parsed = applyScheduleOffset(parsed);
        if (dao.existsByScenarioFile(scenarioFile)) {
            throw new DummyAlreadyInsertedException(scenarioFile);
        }
        Duration appliedDuration = calculateOriginalDuration(parsed);
        WriteResult result = dao.insertAll(scenarioFile, parsed, properties.getGuestPasswordHash());
        return new DummySqlInsertResponse(
                fileSeq,
                scenarioFile,
                parsed.scenarios().size(),
                result.postCount(),
                result.commentCount(),
                STATUS_INSERTED,
                findEarliestScheduledAt(parsed),
                appliedDuration
        );
    }

    private void validateFileSeq(int fileSeq) {
        if (fileSeq < 1) {
            throw new InvalidDummyScenarioException("fileSeq는 1 이상이어야 합니다: " + fileSeq);
        }
    }

    private ScenarioFile parse(String scenarioFile) {
        Resource resource = resolveScenarioResource(scenarioFile);
        if (!resource.exists()) {
            throw new DummyScenarioFileNotFoundException(scenarioFile);
        }
        try (InputStream input = resource.getInputStream()) {
            return scenarioLoader.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("시나리오 파일을 읽지 못했습니다: " + scenarioFile, e);
        } catch (RuntimeException e) {
            throw new InvalidDummyScenarioException("유효하지 않은 시나리오 파일입니다: " + scenarioFile, e);
        }
    }

    private Resource resolveScenarioResource(String scenarioFile) {
        Resource builtIn = resourceResolver.getResource(properties.getScenariosBasePath() + scenarioFile);
        if (builtIn.exists()) {
            return builtIn;
        }
        return resourceResolver.getResource(uploadResourcePrefix() + scenarioFile);
    }

    private String builtInPattern() {
        String pattern = properties.getScenariosBasePath() + SCENARIO_FILE_PREFIX + "*" + SCENARIO_FILE_SUFFIX;
        if (pattern.startsWith(CLASSPATH_PREFIX)) {
            return CLASSPATH_ALL_PREFIX + pattern.substring(CLASSPATH_PREFIX.length());
        }
        return pattern;
    }

    private String uploadPattern() {
        return uploadResourcePrefix() + SCENARIO_FILE_PREFIX + "*" + SCENARIO_FILE_SUFFIX;
    }

    private String uploadResourcePrefix() {
        String path = normalizeTrailingSlash(properties.getUploadPath());
        if (path.startsWith(FILE_PREFIX) || path.startsWith(CLASSPATH_PREFIX)) {
            return path;
        }
        return FILE_PREFIX + path;
    }

    private Path uploadFilesystemPath() {
        String path = properties.getUploadPath();
        if (path.startsWith(FILE_PREFIX)) {
            path = path.substring(FILE_PREFIX.length());
        }
        return Paths.get(path);
    }

    private String normalizeTrailingSlash(String path) {
        return path.endsWith("/") ? path : path + "/";
    }

    private void ensureUploadDirectory() {
        Path dir = uploadFilesystemPath();
        if (Files.exists(dir)) {
            return;
        }
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new IllegalStateException("upload-path 디렉토리를 생성하지 못했습니다: " + dir, e);
        }
    }

    private DummySqlInsertStatusResponse toStatusResponse(String scenarioFile) {
        Matcher matcher = SCENARIO_FILE_PATTERN.matcher(scenarioFile);
        if (!matcher.matches()) {
            throw new InvalidDummyScenarioException("잘못된 시나리오 파일명입니다: " + scenarioFile);
        }
        int fileSeq = Integer.parseInt(matcher.group(1));
        return buildStatusResponse(fileSeq, scenarioFile);
    }

    private DummySqlInsertStatusResponse buildStatusResponse(int fileSeq, String scenarioFile) {
        ScenarioFile parsed = parse(scenarioFile);
        Duration originalDuration = calculateOriginalDuration(parsed);
        boolean inserted = dao.existsByScenarioFile(scenarioFile);
        OffsetDateTime appliedStartAt = inserted
                ? dao.findEarliestScheduledAt(scenarioFile).orElse(null)
                : null;
        return new DummySqlInsertStatusResponse(fileSeq, scenarioFile, inserted, appliedStartAt, originalDuration);
    }

    private DummyScenarioPreviewResponse toPreviewResponse(int fileSeq, String scenarioFile, ScenarioFile file) {
        List<PostPreview> posts = file.scenarios().stream()
                .map(this::toPostPreview)
                .toList();
        return new DummyScenarioPreviewResponse(fileSeq, scenarioFile, calculateOriginalDuration(file), posts);
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
        BigInteger offsetNanos = BigInteger.valueOf(originalOffset.toNanos());
        BigInteger newDurationNanos = BigInteger.valueOf(newDuration.toNanos());
        BigInteger originalDurationNanos = BigInteger.valueOf(originalDuration.toNanos());
        BigInteger scaledNanos = offsetNanos.multiply(newDurationNanos).divide(originalDurationNanos);
        return Duration.ofNanos(scaledNanos.longValueExact());
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
}
