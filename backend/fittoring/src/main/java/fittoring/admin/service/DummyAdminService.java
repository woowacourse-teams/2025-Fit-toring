package fittoring.admin.service;

import fittoring.admin.config.DummyAdminApiProperties;
import fittoring.admin.exception.DummyAlreadyInsertedException;
import fittoring.admin.exception.DummyScenarioFileNotFoundException;
import fittoring.admin.exception.InvalidDummyScenarioException;
import fittoring.admin.presentation.dto.DummySqlInsertResponse;
import fittoring.admin.presentation.dto.DummySqlInsertStatusResponse;
import fittoring.admin.repository.DummyPendingDao;
import fittoring.admin.repository.DummyPendingDao.WriteResult;
import fittoring.application.community.dummy.scenario.Scenario;
import fittoring.application.community.dummy.scenario.ScenarioComment;
import fittoring.application.community.dummy.scenario.ScenarioFile;
import fittoring.application.community.dummy.scenario.ScenarioLoader;
import fittoring.application.community.dummy.scenario.ScenarioPost;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DummyAdminService {

    private static final String SCENARIO_FILE_PREFIX = "scenarios";
    private static final String SCENARIO_FILE_SUFFIX = ".yml";
    private static final String CLASSPATH_PREFIX = "classpath:";
    private static final String CLASSPATH_ALL_PREFIX = "classpath*:";
    private static final Pattern SCENARIO_FILE_PATTERN = Pattern.compile("^scenarios(\\d+)\\.yml$");
    private static final String STATUS_INSERTED = "INSERTED";

    private final DummyPendingDao dao;
    private final ResourcePatternResolver resourceResolver;
    private final ScenarioLoader scenarioLoader;
    private final DummyAdminApiProperties properties;

    public List<DummySqlInsertStatusResponse> list() {
        try {
            return Arrays.stream(resourceResolver.getResources(scenarioFilesPattern()))
                    .map(Resource::getFilename)
                    .filter(fileName -> fileName != null && SCENARIO_FILE_PATTERN.matcher(fileName).matches())
                    .map(this::toStatusResponse)
                    .sorted((left, right) -> Integer.compare(left.fileSeq(), right.fileSeq()))
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

    public DummySqlInsertResponse insert(int fileSeq) {
        return insert(fileSeq, null);
    }

    public DummySqlInsertResponse insert(int fileSeq, OffsetDateTime startAt) {
        validateFileSeq(fileSeq);
        String scenarioFile = SCENARIO_FILE_PREFIX + fileSeq + SCENARIO_FILE_SUFFIX;
        ScenarioFile parsed = applyScheduleOffset(applyStartAt(parse(scenarioFile), startAt));
        if (dao.existsByScenarioFile(scenarioFile)) {
            throw new DummyAlreadyInsertedException(scenarioFile);
        }
        WriteResult result = dao.insertAll(scenarioFile, parsed, properties.getGuestPasswordHash());
        return new DummySqlInsertResponse(
                fileSeq,
                scenarioFile,
                parsed.scenarios().size(),
                result.postCount(),
                result.commentCount(),
                STATUS_INSERTED,
                findEarliestScheduledAt(parsed)
        );
    }

    private void validateFileSeq(int fileSeq) {
        if (fileSeq < 1) {
            throw new InvalidDummyScenarioException("fileSeq는 1 이상이어야 합니다: " + fileSeq);
        }
    }

    private ScenarioFile parse(String scenarioFile) {
        Resource resource = resourceResolver.getResource(properties.getScenariosBasePath() + scenarioFile);
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

    private String scenarioFilesPattern() {
        String pattern = properties.getScenariosBasePath() + SCENARIO_FILE_PREFIX + "*" + SCENARIO_FILE_SUFFIX;
        if (pattern.startsWith(CLASSPATH_PREFIX)) {
            return CLASSPATH_ALL_PREFIX + pattern.substring(CLASSPATH_PREFIX.length());
        }
        return pattern;
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
        boolean inserted = dao.existsByScenarioFile(scenarioFile);
        OffsetDateTime appliedStartAt = inserted
                ? dao.findEarliestScheduledAt(scenarioFile).orElse(null)
                : null;
        return new DummySqlInsertStatusResponse(fileSeq, scenarioFile, inserted, appliedStartAt);
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
