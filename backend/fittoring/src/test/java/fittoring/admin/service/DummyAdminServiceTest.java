package fittoring.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fittoring.admin.config.DummyAdminApiProperties;
import fittoring.admin.exception.DummyAlreadyInsertedException;
import fittoring.admin.exception.DummyScenarioFileNotFoundException;
import fittoring.admin.exception.InvalidDummyScenarioException;
import fittoring.admin.presentation.dto.DummySqlInsertResponse;
import fittoring.admin.presentation.dto.DummySqlInsertStatusResponse;
import fittoring.admin.repository.DummyPendingDao;
import fittoring.admin.repository.DummyPendingDao.WriteResult;
import fittoring.application.community.dummy.scenario.ScenarioFile;
import fittoring.application.community.dummy.scenario.ScenarioLoader;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

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

    @Mock
    private DummyPendingDao dao;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private Resource resource;

    private final ScenarioLoader scenarioLoader = new ScenarioLoader();

    private final DummyAdminApiProperties properties = new DummyAdminApiProperties(
            true, BASE_PATH, GUEST_HASH, NO_OFFSET_DAYS);

    private DummyAdminService service;

    @BeforeEach
    void setUp() {
        service = new DummyAdminService(dao, resourceLoader, scenarioLoader, properties);
    }

    @DisplayName("정상 흐름: yml을 적재하고 응답 DTO를 반환한다.")
    @Test
    void inserts() throws Exception {
        // given
        when(resourceLoader.getResource(BASE_PATH + FILE_1)).thenReturn(resource);
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
    }

    @DisplayName("schedule-offset-days가 있으면 모든 scheduled_at을 해당 일수만큼 미뤄서 적재한다.")
    @Test
    void insertsWithScheduleOffset() throws Exception {
        // given
        DummyAdminService offsetService = new DummyAdminService(
                dao,
                resourceLoader,
                scenarioLoader,
                new DummyAdminApiProperties(true, BASE_PATH, GUEST_HASH, PROD_OFFSET_DAYS)
        );
        when(resourceLoader.getResource(BASE_PATH + FILE_1)).thenReturn(resource);
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
        when(resourceLoader.getResource(BASE_PATH + FILE_1)).thenReturn(resource);
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
        when(resourceLoader.getResource(BASE_PATH + FILE_99)).thenReturn(resource);
        when(resource.exists()).thenReturn(false);

        // when // then
        assertThatThrownBy(() -> service.insert(99))
                .isInstanceOf(DummyScenarioFileNotFoundException.class);
        verify(dao, never()).insertAll(any(), any(), any());
    }

    @DisplayName("이미 적재된 파일이면 예외를 던진다.")
    @Test
    void throwsWhenAlreadyInserted() throws Exception {
        // given
        when(resourceLoader.getResource(BASE_PATH + FILE_1)).thenReturn(resource);
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

    @DisplayName("status: 적재 여부를 그대로 반환한다.")
    @Test
    void returnsStatus() {
        // given
        when(dao.existsByScenarioFile(FILE_1)).thenReturn(true);

        // when
        DummySqlInsertStatusResponse response = service.status(1);

        // then
        assertThat(response.fileSeq()).isEqualTo(1);
        assertThat(response.scenarioFile()).isEqualTo(FILE_1);
        assertThat(response.inserted()).isTrue();
    }

    @DisplayName("status: 적재되지 않은 파일이면 inserted=false.")
    @Test
    void returnsStatusForNotInserted() {
        // given
        when(dao.existsByScenarioFile(FILE_2)).thenReturn(false);

        // when
        DummySqlInsertStatusResponse response = service.status(2);

        // then
        assertThat(response.inserted()).isFalse();
    }

    @DisplayName("status: fileSeq가 0 이하이면 예외를 던진다.")
    @Test
    void statusRejectsNonPositiveFileSeq() {
        assertThatThrownBy(() -> service.status(0))
                .isInstanceOf(InvalidDummyScenarioException.class);
    }

    private ByteArrayInputStream yamlStream(String yaml) {
        return new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));
    }
}
