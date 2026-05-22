package fittoring.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fittoring.admin.config.DummyAdminApiProperties;
import fittoring.admin.exception.DummyAlreadyInsertedException;
import fittoring.admin.exception.DummyScenarioFileAlreadyExistsException;
import fittoring.admin.exception.DummyScenarioFileNotFoundException;
import fittoring.admin.exception.InvalidDummyScenarioException;
import fittoring.admin.presentation.dto.DummyScenarioPreviewResponse;
import fittoring.admin.presentation.dto.DummySqlInsertResponse;
import fittoring.admin.presentation.dto.DummySqlInsertStatusResponse;
import fittoring.admin.repository.DummyPendingDao;
import fittoring.admin.repository.DummyPendingDao.WriteResult;
import fittoring.application.community.dummy.scenario.ScenarioFile;
import fittoring.application.community.dummy.scenario.ScenarioLoader;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.stream.Stream;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class DummyAdminServiceTest {

    private static final String BASE_PATH = "classpath:dummy/";
    private static final String GUEST_HASH = "hash123";
    private static final String FILE_1 = "scenarios1.yml";
    private static final String FILE_2 = "scenarios2.yml";
    private static final String FILE_99 = "scenarios99.yml";
    private static final String STATUS_INSERTED = "INSERTED";
    private static final int NO_OFFSET_DAYS = 0;
    private static final int PROD_OFFSET_DAYS = 2;

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
    private DummyPendingDao dao;

    @Mock
    private ResourcePatternResolver resourceResolver;

    @Mock
    private Resource resource;

    @Mock
    private Resource resourceTwo;

    private final ScenarioLoader scenarioLoader = new ScenarioLoader();

    @TempDir
    Path tempUploadDir;

    private String uploadPath;
    private DummyAdminApiProperties properties;
    private DummyAdminService service;

    @BeforeEach
    void setUp() throws Exception {
        uploadPath = tempUploadDir.toString().replace('\\', '/') + "/";
        properties = new DummyAdminApiProperties(true, BASE_PATH, uploadPath, GUEST_HASH, NO_OFFSET_DAYS);
        service = new DummyAdminService(dao, resourceResolver, scenarioLoader, properties);

        // 기본: 어떤 패턴이든 빈 결과, 어떤 경로의 Resource든 not-exists. 개별 테스트에서 specific stub으로 override.
        lenient().when(resourceResolver.getResources(anyString())).thenReturn(new Resource[0]);
        Resource notExistsResource = mock(Resource.class);
        lenient().when(notExistsResource.exists()).thenReturn(false);
        lenient().when(resourceResolver.getResource(anyString())).thenReturn(notExistsResource);
    }

    @DisplayName("준비된 시나리오 파일 목록과 적재 상태를 반환한다.")
    @Test
    void listsScenarioFiles() throws Exception {
        // given
        OffsetDateTime appliedStartAt = OffsetDateTime.parse("2026-05-04T15:00:00+09:00");
        when(resourceResolver.getResources("classpath*:dummy/scenarios*.yml"))
                .thenReturn(new Resource[]{resourceTwo, resource});
        when(resource.getFilename()).thenReturn(FILE_1);
        when(resourceTwo.getFilename()).thenReturn(FILE_2);
        stubScenarioFile(FILE_1, resource, VALID_YAML);
        stubScenarioFile(FILE_2, resourceTwo, VALID_YAML);
        when(dao.existsByScenarioFile(FILE_1)).thenReturn(true);
        when(dao.existsByScenarioFile(FILE_2)).thenReturn(false);
        when(dao.findEarliestScheduledAt(FILE_1)).thenReturn(Optional.of(appliedStartAt));

        // when
        var responses = service.list();

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).fileSeq()).isEqualTo(1);
        assertThat(responses.get(0).scenarioFile()).isEqualTo(FILE_1);
        assertThat(responses.get(0).inserted()).isTrue();
        assertThat(responses.get(0).appliedStartAt()).isEqualTo(appliedStartAt);
        assertThat(responses.get(0).originalDuration()).isEqualTo(Duration.ofMinutes(5));
        assertThat(responses.get(1).fileSeq()).isEqualTo(2);
        assertThat(responses.get(1).scenarioFile()).isEqualTo(FILE_2);
        assertThat(responses.get(1).inserted()).isFalse();
        assertThat(responses.get(1).appliedStartAt()).isNull();
        assertThat(responses.get(1).originalDuration()).isEqualTo(Duration.ofMinutes(5));
    }

    @DisplayName("preview: yml 내용을 게시글과 댓글 트리로 반환하고 원본 기간을 계산한다.")
    @Test
    void previewsScenarioFile() throws Exception {
        // given
        stubScenarioFile(FILE_1, resource, PREVIEW_YAML);

        // when
        DummyScenarioPreviewResponse response = service.preview(1);

        // then
        assertThat(response.fileSeq()).isEqualTo(1);
        assertThat(response.scenarioFile()).isEqualTo(FILE_1);
        assertThat(response.originalDuration()).isEqualTo(Duration.ofMinutes(40));
        assertThat(response.posts()).hasSize(1);
        assertThat(response.posts().getFirst().nickname()).isEqualTo("글쓴이");
        assertThat(response.posts().getFirst().scheduledAt())
                .isEqualTo(OffsetDateTime.parse("2026-05-04T15:00:00+09:00"));
        assertThat(response.posts().getFirst().title()).isEqualTo("미리보기 제목");
        assertThat(response.posts().getFirst().content()).isEqualTo("미리보기 본문");
        assertThat(response.posts().getFirst().comments()).hasSize(2);
        assertThat(response.posts().getFirst().comments().getFirst().content()).isEqualTo("첫 댓글");
        assertThat(response.posts().getFirst().comments().getFirst().replies()).hasSize(1);
        assertThat(response.posts().getFirst().comments().getFirst().replies().getFirst().content()).isEqualTo("답글");
        assertThat(response.posts().getFirst().comments().get(1).scheduledAt())
                .isEqualTo(OffsetDateTime.parse("2026-05-04T15:40:00+09:00"));
    }

    @DisplayName("preview: 비어 있는 시나리오 파일이면 invalid scenario 예외를 던진다.")
    @Test
    void previewThrowsWhenScenarioFileEmpty() throws Exception {
        // given
        String emptyYaml = """
                scenarios: []
                """;
        stubScenarioFile(FILE_1, resource, emptyYaml);

        // when // then
        assertThatThrownBy(() -> service.preview(1))
                .isInstanceOf(InvalidDummyScenarioException.class);
    }

    @DisplayName("정상 흐름: yml을 적재하고 응답 DTO를 반환한다.")
    @Test
    void inserts() throws Exception {
        // given
        when(resourceResolver.getResource(BASE_PATH + FILE_1)).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(yamlStream(VALID_YAML));
        when(dao.existsByScenarioFile(FILE_1)).thenReturn(false);
        when(dao.insertAll(eq(FILE_1), any(), eq(GUEST_HASH)))
                .thenReturn(new WriteResult(1, 1));

        // when
        DummySqlInsertResponse response = service.insert(1);

        // then
        assertThat(response.fileSeq()).isEqualTo(1);
        assertThat(response.scenarioFile()).isEqualTo(FILE_1);
        assertThat(response.insertedScenarioCount()).isEqualTo(1);
        assertThat(response.insertedPostPendingCount()).isEqualTo(1);
        assertThat(response.insertedCommentPendingCount()).isEqualTo(1);
        assertThat(response.status()).isEqualTo(STATUS_INSERTED);
        assertThat(response.appliedStartAt())
                .isEqualTo(OffsetDateTime.parse("2026-05-04T15:00:00+09:00"));
        assertThat(response.appliedDuration()).isEqualTo(Duration.ofMinutes(5));
    }

    @DisplayName("duration이 있으면 원본 기간 대비 비례 스케일링해서 적재한다.")
    @Test
    void insertsWithDuration() throws Exception {
        // given
        stubScenarioFile(FILE_1, resource, PREVIEW_YAML);
        when(dao.existsByScenarioFile(FILE_1)).thenReturn(false);
        when(dao.insertAll(eq(FILE_1), any(), eq(GUEST_HASH)))
                .thenReturn(new WriteResult(1, 3));

        // when
        DummySqlInsertResponse response = service.insert(1, null, Duration.ofMinutes(80));

        // then
        ArgumentCaptor<ScenarioFile> captor = ArgumentCaptor.forClass(ScenarioFile.class);
        verify(dao).insertAll(eq(FILE_1), captor.capture(), eq(GUEST_HASH));
        ScenarioFile scaled = captor.getValue();
        assertThat(scaled.scenarios().getFirst().post().scheduledAt())
                .isEqualTo(OffsetDateTime.parse("2026-05-04T15:00:00+09:00"));
        assertThat(scaled.scenarios().getFirst().comments().getFirst().scheduledAt())
                .isEqualTo(OffsetDateTime.parse("2026-05-04T15:20:00+09:00"));
        assertThat(scaled.scenarios().getFirst().comments().getFirst().replies().getFirst().scheduledAt())
                .isEqualTo(OffsetDateTime.parse("2026-05-04T15:50:00+09:00"));
        assertThat(scaled.scenarios().getFirst().comments().get(1).scheduledAt())
                .isEqualTo(OffsetDateTime.parse("2026-05-04T16:20:00+09:00"));
        assertThat(response.appliedDuration()).isEqualTo(Duration.ofMinutes(80));
    }

    @DisplayName("duration 스케일링 후 startAt을 적용한다.")
    @Test
    void insertsWithDurationAndStartAt() throws Exception {
        // given
        stubScenarioFile(FILE_1, resource, VALID_YAML);
        when(dao.existsByScenarioFile(FILE_1)).thenReturn(false);
        when(dao.insertAll(eq(FILE_1), any(), eq(GUEST_HASH)))
                .thenReturn(new WriteResult(1, 1));

        // when
        service.insert(
                1,
                OffsetDateTime.parse("2026-05-04T17:30:00+09:00"),
                Duration.ofMinutes(10)
        );

        // then
        ArgumentCaptor<ScenarioFile> captor = ArgumentCaptor.forClass(ScenarioFile.class);
        verify(dao).insertAll(eq(FILE_1), captor.capture(), eq(GUEST_HASH));
        ScenarioFile shifted = captor.getValue();
        assertThat(shifted.scenarios().getFirst().post().scheduledAt())
                .isEqualTo(OffsetDateTime.parse("2026-05-04T17:30:00+09:00"));
        assertThat(shifted.scenarios().getFirst().comments().getFirst().scheduledAt())
                .isEqualTo(OffsetDateTime.parse("2026-05-04T17:40:00+09:00"));
    }

    @DisplayName("duration이 0 이하이면 invalid scenario 예외를 던진다.")
    @Test
    void rejectsNonPositiveDuration() throws Exception {
        // given
        stubScenarioFile(FILE_1, resource, VALID_YAML);

        // when // then
        assertThatThrownBy(() -> service.insert(1, null, Duration.ZERO))
                .isInstanceOf(InvalidDummyScenarioException.class);
        assertThatThrownBy(() -> service.insert(1, null, Duration.ofMinutes(-1)))
                .isInstanceOf(InvalidDummyScenarioException.class);
        verify(dao, never()).insertAll(any(), any(), any());
    }

    @DisplayName("duration이 너무 커서 산술 오버플로우가 발생하면 invalid scenario 예외를 던진다.")
    @Test
    void rejectsDurationCausingArithmeticOverflow() throws Exception {
        // given
        stubScenarioFile(FILE_1, resource, VALID_YAML);
        Duration tooLargeDuration = Duration.ofMillis(Long.MAX_VALUE);

        // when // then
        assertThatThrownBy(() -> service.insert(1, null, tooLargeDuration))
                .isInstanceOf(InvalidDummyScenarioException.class);
        verify(dao, never()).insertAll(any(), any(), any());
    }

    @DisplayName("원본 duration이 0인데 duration 입력이 있으면 invalid scenario 예외를 던진다.")
    @Test
    void rejectsDurationWhenOriginalDurationIsZero() throws Exception {
        // given
        String zeroDurationYaml = """
                scenarios:
                  - post:
                      nickname: "글쓴이"
                      scheduled_at: "2026-05-04T15:00:00+09:00"
                      title: "단일 글"
                      content: "단일 글"
                    comments: []
                """;
        stubScenarioFile(FILE_1, resource, zeroDurationYaml);

        // when // then
        assertThatThrownBy(() -> service.insert(1, null, Duration.ofMinutes(10)))
                .isInstanceOf(InvalidDummyScenarioException.class);
        verify(dao, never()).insertAll(any(), any(), any());
    }

    @DisplayName("schedule-offset-days가 있으면 모든 scheduled_at을 해당 일수만큼 미뤄서 적재한다.")
    @Test
    void insertsWithScheduleOffset() throws Exception {
        // given
        DummyAdminService offsetService = new DummyAdminService(
                dao,
                resourceResolver,
                scenarioLoader,
                new DummyAdminApiProperties(true, BASE_PATH, uploadPath, GUEST_HASH, PROD_OFFSET_DAYS)
        );
        when(resourceResolver.getResource(BASE_PATH + FILE_1)).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(yamlStream(VALID_YAML));
        when(dao.existsByScenarioFile(FILE_1)).thenReturn(false);
        when(dao.insertAll(eq(FILE_1), any(), eq(GUEST_HASH)))
                .thenReturn(new WriteResult(1, 1));

        // when
        offsetService.insert(1);

        // then
        ArgumentCaptor<ScenarioFile> captor = ArgumentCaptor.forClass(ScenarioFile.class);
        verify(dao).insertAll(eq(FILE_1), captor.capture(), eq(GUEST_HASH));
        ScenarioFile shifted = captor.getValue();
        assertThat(shifted.scenarios().getFirst().post().scheduledAt())
                .isEqualTo(OffsetDateTime.parse("2026-05-06T15:00:00+09:00"));
        assertThat(shifted.scenarios().getFirst().comments().getFirst().scheduledAt())
                .isEqualTo(OffsetDateTime.parse("2026-05-06T15:05:00+09:00"));
    }

    @DisplayName("startAt이 있으면 파일의 첫 scheduled_at을 기준으로 전체 시나리오 시간을 평행 이동한다.")
    @Test
    void insertsWithStartAt() throws Exception {
        // given
        when(resourceResolver.getResource(BASE_PATH + FILE_1)).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(yamlStream(VALID_YAML));
        when(dao.existsByScenarioFile(FILE_1)).thenReturn(false);
        when(dao.insertAll(eq(FILE_1), any(), eq(GUEST_HASH)))
                .thenReturn(new WriteResult(1, 1));

        // when
        service.insert(1, OffsetDateTime.parse("2026-05-04T17:30:00+09:00"));

        // then
        ArgumentCaptor<ScenarioFile> captor = ArgumentCaptor.forClass(ScenarioFile.class);
        verify(dao).insertAll(eq(FILE_1), captor.capture(), eq(GUEST_HASH));
        ScenarioFile shifted = captor.getValue();
        assertThat(shifted.scenarios().getFirst().post().scheduledAt())
                .isEqualTo(OffsetDateTime.parse("2026-05-04T17:30:00+09:00"));
        assertThat(shifted.scenarios().getFirst().comments().getFirst().scheduledAt())
                .isEqualTo(OffsetDateTime.parse("2026-05-04T17:35:00+09:00"));
    }

    @DisplayName("파일이 없으면 예외를 던진다.")
    @Test
    void throwsWhenFileNotFound() {
        // given
        when(resourceResolver.getResource(BASE_PATH + FILE_99)).thenReturn(resource);
        when(resource.exists()).thenReturn(false);

        // when // then
        assertThatThrownBy(() -> service.insert(99))
                .isInstanceOf(DummyScenarioFileNotFoundException.class);
        verify(dao, never()).insertAll(any(), any(), any());
    }

    @DisplayName("scheduled_at 형식이 잘못된 yml이면 invalid scenario 예외를 던진다.")
    @Test
    void throwsInvalidScenarioWhenScheduledAtMalformed() throws Exception {
        // given
        String invalidYaml = """
                scenarios:
                  - post:
                      nickname: "글쓴이"
                      scheduled_at: "not-date-time"
                      title: "샘플"
                      content: "샘플"
                    comments: []
                """;
        when(resourceResolver.getResource(BASE_PATH + FILE_1)).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(yamlStream(invalidYaml));

        // when // then
        assertThatThrownBy(() -> service.insert(1))
                .isInstanceOf(InvalidDummyScenarioException.class);
        verify(dao, never()).insertAll(any(), any(), any());
    }

    @DisplayName("yml 구조가 잘못되면 invalid scenario 예외를 던진다.")
    @Test
    void throwsInvalidScenarioWhenYamlShapeInvalid() throws Exception {
        // given
        String invalidYaml = """
                scenarios: wrong-shape
                """;
        when(resourceResolver.getResource(BASE_PATH + FILE_1)).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(yamlStream(invalidYaml));

        // when // then
        assertThatThrownBy(() -> service.insert(1))
                .isInstanceOf(InvalidDummyScenarioException.class);
        verify(dao, never()).insertAll(any(), any(), any());
    }

    @DisplayName("이미 적재된 파일이면 예외를 던진다.")
    @Test
    void throwsWhenAlreadyInserted() throws Exception {
        // given
        when(resourceResolver.getResource(BASE_PATH + FILE_1)).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(yamlStream(VALID_YAML));
        when(dao.existsByScenarioFile(FILE_1)).thenReturn(true);

        // when // then
        assertThatThrownBy(() -> service.insert(1))
                .isInstanceOf(DummyAlreadyInsertedException.class);
        verify(dao, never()).insertAll(any(), any(), any());
    }

    @DisplayName("fileSeq가 0 이하이면 예외를 던진다.")
    @Test
    void rejectsNonPositiveFileSeq() {
        assertThatThrownBy(() -> service.insert(0))
                .isInstanceOf(InvalidDummyScenarioException.class);
        assertThatThrownBy(() -> service.insert(-1))
                .isInstanceOf(InvalidDummyScenarioException.class);
    }

    @DisplayName("status: 적재 여부를 그대로 반환하고 적재 시작 시각을 포함한다.")
    @Test
    void returnsStatus() throws Exception {
        // given
        OffsetDateTime appliedStartAt = OffsetDateTime.parse("2026-05-04T15:00:00+09:00");
        stubScenarioFile(FILE_1, resource, VALID_YAML);
        when(dao.existsByScenarioFile(FILE_1)).thenReturn(true);
        when(dao.findEarliestScheduledAt(FILE_1)).thenReturn(Optional.of(appliedStartAt));

        // when
        DummySqlInsertStatusResponse response = service.status(1);

        // then
        assertThat(response.fileSeq()).isEqualTo(1);
        assertThat(response.scenarioFile()).isEqualTo(FILE_1);
        assertThat(response.inserted()).isTrue();
        assertThat(response.appliedStartAt()).isEqualTo(appliedStartAt);
        assertThat(response.originalDuration()).isEqualTo(Duration.ofMinutes(5));
    }

    @DisplayName("status: 적재되지 않은 파일이면 inserted=false이고 appliedStartAt은 null이다.")
    @Test
    void returnsStatusForNotInserted() throws Exception {
        // given
        stubScenarioFile(FILE_2, resourceTwo, VALID_YAML);
        when(dao.existsByScenarioFile(FILE_2)).thenReturn(false);

        // when
        DummySqlInsertStatusResponse response = service.status(2);

        // then
        assertThat(response.inserted()).isFalse();
        assertThat(response.appliedStartAt()).isNull();
        assertThat(response.originalDuration()).isEqualTo(Duration.ofMinutes(5));
        verify(dao, never()).findEarliestScheduledAt(any());
    }

    @DisplayName("status: fileSeq가 0 이하이면 예외를 던진다.")
    @Test
    void statusRejectsNonPositiveFileSeq() {
        assertThatThrownBy(() -> service.status(0))
                .isInstanceOf(InvalidDummyScenarioException.class);
    }

    @DisplayName("list: 빌트인 시나리오와 업로드된 시나리오를 모두 반환한다.")
    @Test
    void listsBuiltInAndUploadedScenarios() throws Exception {
        // given
        when(resourceResolver.getResources("classpath*:dummy/scenarios*.yml"))
                .thenReturn(new Resource[]{resource});
        when(resourceResolver.getResources(uploadPatternUrl()))
                .thenReturn(new Resource[]{resourceTwo});
        when(resource.getFilename()).thenReturn(FILE_1);
        when(resourceTwo.getFilename()).thenReturn("scenarios5.yml");
        stubScenarioFile(FILE_1, resource, VALID_YAML);
        stubUploadedScenarioFile("scenarios5.yml", resourceTwo, VALID_YAML);

        // when
        var responses = service.list();

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).fileSeq()).isEqualTo(1);
        assertThat(responses.get(1).fileSeq()).isEqualTo(5);
        assertThat(responses.get(1).scenarioFile()).isEqualTo("scenarios5.yml");
    }

    @DisplayName("preview: 업로드된 시나리오 파일도 미리보기할 수 있다.")
    @Test
    void previewsUploadedScenario() throws Exception {
        // given
        when(resourceResolver.getResource(BASE_PATH + "scenarios5.yml")).thenReturn(resource);
        when(resource.exists()).thenReturn(false);
        stubUploadedScenarioFile("scenarios5.yml", resourceTwo, VALID_YAML);

        // when
        var response = service.preview(5);

        // then
        assertThat(response.fileSeq()).isEqualTo(5);
        assertThat(response.scenarioFile()).isEqualTo("scenarios5.yml");
        assertThat(response.posts()).hasSize(1);
    }

    @DisplayName("upload: YAML 파일을 업로드하면 다음 fileSeq로 저장하고 상태를 반환한다.")
    @Test
    void uploadsScenarioFile() throws Exception {
        // given: 빌트인에 1, 2번이 있다고 가정
        when(resourceResolver.getResources("classpath*:dummy/scenarios*.yml"))
                .thenReturn(new Resource[]{resource, resourceTwo});
        when(resource.getFilename()).thenReturn(FILE_1);
        when(resourceTwo.getFilename()).thenReturn(FILE_2);
        MockMultipartFile file = new MockMultipartFile(
                "file", "my-scenario.yml", "text/yaml", VALID_YAML.getBytes(StandardCharsets.UTF_8));

        // when
        DummySqlInsertStatusResponse response = service.upload(file);

        // then
        assertThat(response.fileSeq()).isEqualTo(3);
        assertThat(response.scenarioFile()).isEqualTo("scenarios3.yml");
        assertThat(response.inserted()).isFalse();
        assertThat(response.appliedStartAt()).isNull();
        assertThat(response.originalDuration()).isEqualTo(Duration.ofMinutes(5));
        assertThat(Files.exists(tempUploadDir.resolve("scenarios3.yml"))).isTrue();
    }

    @DisplayName("upload: 빌트인이 없으면 fileSeq 1부터 시작한다.")
    @Test
    void uploadStartsFromOneWhenNoExistingScenarios() throws Exception {
        // given: 빌트인 / upload 모두 비어있음 (setUp default)
        MockMultipartFile file = new MockMultipartFile(
                "file", "first.yml", "text/yaml", VALID_YAML.getBytes(StandardCharsets.UTF_8));

        // when
        DummySqlInsertStatusResponse response = service.upload(file);

        // then
        assertThat(response.fileSeq()).isEqualTo(1);
        assertThat(response.scenarioFile()).isEqualTo("scenarios1.yml");
        assertThat(Files.exists(tempUploadDir.resolve("scenarios1.yml"))).isTrue();
    }

    @DisplayName("upload: 확장자가 .yml/.yaml이 아니면 거부한다.")
    @Test
    void uploadRejectsNonYamlExtension() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "scenario.txt", "text/plain", VALID_YAML.getBytes(StandardCharsets.UTF_8));

        assertThatThrownBy(() -> service.upload(file))
                .isInstanceOf(InvalidDummyScenarioException.class);
        assertThat(Files.list(tempUploadDir).count()).isZero();
    }

    @DisplayName("upload: 같은 이름의 시나리오 파일이 이미 있으면 거부하고 기존 파일을 덮어쓰지 않는다.")
    @Test
    void uploadRejectsWhenSameFileNameExists() throws Exception {
        // given: 빌트인 1, 2 → nextFileSeq = 3. 그런데 upload-path에 이미 scenarios3.yml이 있음.
        when(resourceResolver.getResources("classpath*:dummy/scenarios*.yml"))
                .thenReturn(new Resource[]{resource, resourceTwo});
        when(resource.getFilename()).thenReturn(FILE_1);
        when(resourceTwo.getFilename()).thenReturn(FILE_2);
        Path existing = tempUploadDir.resolve("scenarios3.yml");
        Files.writeString(existing, "preexisting content");

        MockMultipartFile file = new MockMultipartFile(
                "file", "incoming.yml", "text/yaml", VALID_YAML.getBytes(StandardCharsets.UTF_8));

        // when // then
        assertThatThrownBy(() -> service.upload(file))
                .isInstanceOf(DummyScenarioFileAlreadyExistsException.class);
        assertThat(Files.readString(existing)).isEqualTo("preexisting content");
    }

    @DisplayName("upload: 시나리오가 비어 있으면 거부하고 파일을 저장하지 않는다.")
    @Test
    void uploadRejectsEmptyScenarios() throws Exception {
        String emptyYaml = "scenarios: []";
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.yml", "text/yaml", emptyYaml.getBytes(StandardCharsets.UTF_8));

        assertThatThrownBy(() -> service.upload(file))
                .isInstanceOf(InvalidDummyScenarioException.class);
        try (Stream<Path> entries = Files.list(tempUploadDir)) {
            assertThat(entries.count()).isZero();
        }
    }

    @DisplayName("upload: 잘못된 YAML이면 거부하고 파일을 저장하지 않는다.")
    @Test
    void uploadRejectsInvalidYaml() throws Exception {
        String invalidYaml = "scenarios: wrong-shape";
        MockMultipartFile file = new MockMultipartFile(
                "file", "bad.yml", "text/yaml", invalidYaml.getBytes(StandardCharsets.UTF_8));

        assertThatThrownBy(() -> service.upload(file))
                .isInstanceOf(InvalidDummyScenarioException.class);
        assertThat(Files.list(tempUploadDir).count()).isZero();
    }

    @DisplayName("insert: 업로드된 시나리오 파일도 적재할 수 있다.")
    @Test
    void insertsUploadedScenario() throws Exception {
        // given
        when(resourceResolver.getResource(BASE_PATH + "scenarios5.yml")).thenReturn(resource);
        when(resource.exists()).thenReturn(false);
        stubUploadedScenarioFile("scenarios5.yml", resourceTwo, VALID_YAML);
        when(dao.existsByScenarioFile("scenarios5.yml")).thenReturn(false);
        when(dao.insertAll(eq("scenarios5.yml"), any(), eq(GUEST_HASH)))
                .thenReturn(new WriteResult(1, 1));

        // when
        var response = service.insert(5);

        // then
        assertThat(response.fileSeq()).isEqualTo(5);
        assertThat(response.scenarioFile()).isEqualTo("scenarios5.yml");
        assertThat(response.insertedScenarioCount()).isEqualTo(1);
    }

    private ByteArrayInputStream yamlStream(String yaml) {
        return new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));
    }

    private void stubScenarioFile(String scenarioFile, Resource scenarioResource, String yaml) throws Exception {
        when(resourceResolver.getResource(BASE_PATH + scenarioFile)).thenReturn(scenarioResource);
        when(scenarioResource.exists()).thenReturn(true);
        when(scenarioResource.getInputStream()).thenAnswer(invocation -> yamlStream(yaml));
    }

    private void stubUploadedScenarioFile(String scenarioFile, Resource scenarioResource, String yaml) throws Exception {
        when(resourceResolver.getResource("file:" + uploadPath + scenarioFile)).thenReturn(scenarioResource);
        when(scenarioResource.exists()).thenReturn(true);
        when(scenarioResource.getInputStream()).thenAnswer(invocation -> yamlStream(yaml));
    }

    private String uploadPatternUrl() {
        return "file:" + uploadPath + "scenarios*.yml";
    }
}
