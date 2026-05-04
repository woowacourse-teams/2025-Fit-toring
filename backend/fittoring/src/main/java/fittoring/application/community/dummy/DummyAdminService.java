package fittoring.application.community.dummy;

import fittoring.application.community.dummy.DummyPendingDao.WriteResult;
import fittoring.application.community.dummy.scenario.ScenarioFile;
import fittoring.application.community.dummy.scenario.ScenarioLoader;
import java.io.IOException;
import java.io.InputStream;
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
        validateFileSeq(fileSeq);
        String scenarioFile = SCENARIO_FILE_PREFIX + fileSeq + SCENARIO_FILE_SUFFIX;
        ScenarioFile parsed = parse(scenarioFile);
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
        } catch (IllegalArgumentException e) {
            throw new InvalidDummyScenarioException(e.getMessage(), e);
        }
    }
}
