package fittoring.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fittoring.admin.config.DummyAdminApiProperties;
import fittoring.admin.exception.DummyAlreadyInsertedException;
import fittoring.admin.exception.InvalidDummyScenarioException;
import fittoring.admin.presentation.dto.DummyScenarioPreviewResponse;
import fittoring.admin.presentation.dto.DummySqlInsertResponse;
import fittoring.admin.presentation.dto.DummySqlInsertStatusResponse;
import fittoring.admin.repository.DummyPendingDao;
import fittoring.admin.repository.DummyPendingDao.WriteResult;
import fittoring.admin.repository.DummyScenarioDao;
import fittoring.admin.repository.DummyScenarioRow;
import fittoring.admin.repository.DummyScenarioStatus;
import fittoring.application.community.dummy.scenario.ScenarioFile;
import fittoring.application.community.dummy.scenario.ScenarioLoader;
import java.time.Duration;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class DummyAdminServiceTest {

    private static final String GUEST_HASH = "hash123";
    private static final String FILENAME = "my-scenario.yml";
    private static final long SCENARIO_ID = 1L;
    private static final OffsetDateTime UPLOADED_AT = OffsetDateTime.parse("2026-05-04T14:00:00+09:00");

    private static final String VALID_YAML = """
            scenarios:
              - post:
                  nickname: "글쓴이"
                  scheduled_at: "2026-05-04T15:00:00+09:00"
                  title: "샘플"
                  content: "샘플"
                comments:
                  - nickname: "댓글러"
                    scheduled_at: "2026-05-04T15:05:00+09:00"
                    content: "루트"
            """;

    private static final String PREVIEW_YAML = """
            scenarios:
              - post:
                  nickname: "글쓴이"
                  scheduled_at: "2026-05-04T15:00:00+09:00"
                  title: "미리보기 제목"
                  content: "미리보기 본문"
                comments:
                  - nickname: "첫 댓글러"
                    scheduled_at: "2026-05-04T15:10:00+09:00"
                    content: "첫 댓글"
                    replies:
                      - nickname: "답글러"
                        scheduled_at: "2026-05-04T15:25:00+09:00"
                        content: "답글"
                  - nickname: "두 번째 댓글러"
                    scheduled_at: "2026-05-04T15:40:00+09:00"
                    content: "두 번째 댓글"
            """;

    @Mock
    private DummyPendingDao pendingDao;

    @Mock
    private DummyScenarioDao scenarioDao;

    private DummyAdminService service;

    @BeforeEach
    void setUp() {
        DummyAdminApiProperties properties = new DummyAdminApiProperties(
                true,
                "classpath:dummy/",
                "./dummy-uploads/",
                GUEST_HASH,
                0
        );
        service = new DummyAdminService(pendingDao, scenarioDao, new ScenarioLoader(), properties);
    }

    @DisplayName("upload: YAML 원문을 dummy_scenario row로 저장하고 상태를 반환한다.")
    @Test
    void uploadsScenarioToDatabase() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                FILENAME,
                "text/yaml",
                VALID_YAML.getBytes()
        );
        when(scenarioDao.save(
                eq(FILENAME),
                any(),
                eq(VALID_YAML),
                any(),
                eq(OffsetDateTime.parse("2026-05-04T15:00:00+09:00")),
                eq(Duration.ofMinutes(5)),
                eq(1),
                eq(1)
        )).thenReturn(SCENARIO_ID);
        when(scenarioDao.getById(SCENARIO_ID)).thenReturn(uploadedScenario(VALID_YAML, Duration.ofMinutes(5), 1, 1));

        DummySqlInsertStatusResponse response = service.upload(file);

        assertThat(response.scenarioId()).isEqualTo(SCENARIO_ID);
        assertThat(response.originalFilename()).isEqualTo(FILENAME);
        assertThat(response.status()).isEqualTo("UPLOADED");
        assertThat(response.originalDuration()).isEqualTo(Duration.ofMinutes(5));
        assertThat(response.postCount()).isEqualTo(1);
        assertThat(response.commentCount()).isEqualTo(1);
    }

    @DisplayName("preview: dummy_scenario의 YAML 원문을 게시글과 댓글 트리로 반환한다.")
    @Test
    void previewsScenarioFromStoredYaml() {
        when(scenarioDao.getById(SCENARIO_ID))
                .thenReturn(uploadedScenario(PREVIEW_YAML, Duration.ofMinutes(40), 1, 3));

        DummyScenarioPreviewResponse response = service.preview(SCENARIO_ID);

        assertThat(response.scenarioId()).isEqualTo(SCENARIO_ID);
        assertThat(response.originalFilename()).isEqualTo(FILENAME);
        assertThat(response.originalDuration()).isEqualTo(Duration.ofMinutes(40));
        assertThat(response.posts()).hasSize(1);
        assertThat(response.posts().getFirst().title()).isEqualTo("미리보기 제목");
        assertThat(response.posts().getFirst().comments()).hasSize(2);
        assertThat(response.posts().getFirst().comments().getFirst().replies()).hasSize(1);
    }

    @DisplayName("insert: scenarioId 기준으로 pending 테이블에 적재하고 scenario 상태를 INSERTED로 바꾼다.")
    @Test
    void insertsScenarioByScenarioId() {
        when(scenarioDao.getById(SCENARIO_ID)).thenReturn(uploadedScenario(VALID_YAML, Duration.ofMinutes(5), 1, 1));
        when(pendingDao.existsByScenarioId(SCENARIO_ID)).thenReturn(false);
        when(pendingDao.insertAll(eq(SCENARIO_ID), any(), eq(GUEST_HASH))).thenReturn(new WriteResult(1, 1));

        DummySqlInsertResponse response = service.insert(SCENARIO_ID, null, null);

        assertThat(response.scenarioId()).isEqualTo(SCENARIO_ID);
        assertThat(response.originalFilename()).isEqualTo(FILENAME);
        assertThat(response.insertedPostPendingCount()).isEqualTo(1);
        assertThat(response.insertedCommentPendingCount()).isEqualTo(1);
        assertThat(response.status()).isEqualTo("INSERTED");
        assertThat(response.appliedStartAt()).isEqualTo(OffsetDateTime.parse("2026-05-04T15:00:00+09:00"));
        assertThat(response.appliedDuration()).isEqualTo(Duration.ofMinutes(5));
        verify(scenarioDao).markInserted(
                eq(SCENARIO_ID),
                any(),
                eq(OffsetDateTime.parse("2026-05-04T15:00:00+09:00")),
                eq(Duration.ofMinutes(5))
        );
    }

    @DisplayName("insert: duration과 startAt을 적용해 scheduledAt을 재계산한다.")
    @Test
    void insertsWithDurationAndStartAt() {
        OffsetDateTime startAt = OffsetDateTime.parse("2026-05-04T17:30:00+09:00");
        when(scenarioDao.getById(SCENARIO_ID)).thenReturn(uploadedScenario(PREVIEW_YAML, Duration.ofMinutes(40), 1, 3));
        when(pendingDao.existsByScenarioId(SCENARIO_ID)).thenReturn(false);
        when(pendingDao.insertAll(eq(SCENARIO_ID), any(), eq(GUEST_HASH))).thenReturn(new WriteResult(1, 3));

        service.insert(SCENARIO_ID, startAt, Duration.ofMinutes(80));

        ArgumentCaptor<ScenarioFile> captor = ArgumentCaptor.forClass(ScenarioFile.class);
        verify(pendingDao).insertAll(eq(SCENARIO_ID), captor.capture(), eq(GUEST_HASH));
        ScenarioFile inserted = captor.getValue();
        assertThat(inserted.scenarios().getFirst().post().scheduledAt()).isEqualTo(startAt);
        assertThat(inserted.scenarios().getFirst().comments().getFirst().scheduledAt())
                .isEqualTo(OffsetDateTime.parse("2026-05-04T17:50:00+09:00"));
        assertThat(inserted.scenarios().getFirst().comments().getFirst().replies().getFirst().scheduledAt())
                .isEqualTo(OffsetDateTime.parse("2026-05-04T18:20:00+09:00"));
    }

    @DisplayName("insert: 이미 적재된 시나리오이면 예외를 던진다.")
    @Test
    void throwsWhenAlreadyInserted() {
        when(scenarioDao.getById(SCENARIO_ID)).thenReturn(insertedScenario());

        assertThatThrownBy(() -> service.insert(SCENARIO_ID, null, null))
                .isInstanceOf(DummyAlreadyInsertedException.class);
    }

    @DisplayName("upload: YAML 확장자가 아니면 예외를 던진다.")
    @Test
    void uploadThrowsWhenExtensionInvalid() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "scenario.txt",
                "text/plain",
                VALID_YAML.getBytes()
        );

        assertThatThrownBy(() -> service.upload(file))
                .isInstanceOf(InvalidDummyScenarioException.class);
    }

    private DummyScenarioRow uploadedScenario(String yaml, Duration originalDuration, int postCount, int commentCount) {
        return new DummyScenarioRow(
                SCENARIO_ID,
                FILENAME,
                "hash",
                yaml,
                DummyScenarioStatus.UPLOADED,
                UPLOADED_AT,
                null,
                OffsetDateTime.parse("2026-05-04T15:00:00+09:00"),
                originalDuration,
                null,
                null,
                postCount,
                commentCount
        );
    }

    private DummyScenarioRow insertedScenario() {
        return new DummyScenarioRow(
                SCENARIO_ID,
                FILENAME,
                "hash",
                VALID_YAML,
                DummyScenarioStatus.INSERTED,
                UPLOADED_AT,
                OffsetDateTime.parse("2026-05-04T14:10:00+09:00"),
                OffsetDateTime.parse("2026-05-04T15:00:00+09:00"),
                Duration.ofMinutes(5),
                OffsetDateTime.parse("2026-05-04T15:00:00+09:00"),
                Duration.ofMinutes(5),
                1,
                1
        );
    }
}
