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
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DummyAdminService {

    private static final String SCENARIO_FILE_PREFIX = "scenarios";
    private static final String SCENARIO_FILE_SUFFIX = ".yml";
    private static final String STATUS_INSERTED = "INSERTED";

    private final DummyPendingDao dao;
    private final ResourceLoader resourceLoader;
    private final ScenarioLoader scenarioLoader;
    private final DummyAdminApiProperties properties;

    public DummySqlInsertStatusResponse status(int fileSeq) {
        validateFileSeq(fileSeq);
        String scenarioFile = SCENARIO_FILE_PREFIX + fileSeq + SCENARIO_FILE_SUFFIX;
        return new DummySqlInsertStatusResponse(fileSeq, scenarioFile, dao.existsByScenarioFile(scenarioFile));
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
                STATUS_INSERTED
        );
    }

    private void validateFileSeq(int fileSeq) {
        if (fileSeq < 1) {
            throw new InvalidDummyScenarioException("fileSeq는 1 이상이어야 합니다: " + fileSeq);
        }
    }

    private ScenarioFile parse(String scenarioFile) {
        Resource resource = resourceLoader.getResource(properties.getScenariosBasePath() + scenarioFile);
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
