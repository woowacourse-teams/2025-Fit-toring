package fittoring.admin.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fittoring.admin.presentation.dto.CommentPreview;
import fittoring.admin.presentation.dto.DummyScenarioPreviewResponse;
import fittoring.admin.presentation.dto.DummySqlInsertResponse;
import fittoring.admin.presentation.dto.DummySqlInsertStatusResponse;
import fittoring.admin.presentation.dto.PostPreview;
import fittoring.admin.service.DummyAdminService;
import fittoring.application.auth.service.JwtExtractor;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.auth.service.TokenPayload;
import fittoring.domain.model.MemberRole;
import fittoring.logging.ErrorJsonLogger;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

@ActiveProfiles("test")
@WebMvcTest(value = DummyAdminController.class, properties = "dummy.admin-api.enabled=true")
class DummyAdminControllerTest {

    private static final String ACCESS_TOKEN = "admin-token";

    @MockitoBean
    private DummyAdminService service;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private JwtExtractor jwtExtractor;

    @MockitoBean
    private ErrorJsonLogger errorJsonLogger;

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("GET /admin/dummy/sql-insert/{scenarioId}/preview: 시나리오 미리보기를 반환한다.")
    @Test
    void previewsScenario() throws Exception {
        // given
        givenAdminAuthentication();
        given(service.preview(1L)).willReturn(new DummyScenarioPreviewResponse(
                1L,
                "my-scenario.yml",
                Duration.ofMinutes(40),
                List.of(new PostPreview(
                        "글쓴이",
                        OffsetDateTime.parse("2026-05-04T15:00:00+09:00"),
                        "미리보기 제목",
                        "미리보기 본문",
                        List.of(new CommentPreview(
                                "댓글러",
                                OffsetDateTime.parse("2026-05-04T15:10:00+09:00"),
                                "댓글",
                                List.of(new CommentPreview(
                                        "답글러",
                                        OffsetDateTime.parse("2026-05-04T15:20:00+09:00"),
                                        "답글",
                                        List.of()
                                ))
                        ))
                ))
        ));

        // when // then
        mockMvc.perform(get("/admin/dummy/sql-insert/1/preview")
                        .cookie(new Cookie("accessToken", ACCESS_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scenarioId").value(1))
                .andExpect(jsonPath("$.originalFilename").value("my-scenario.yml"))
                .andExpect(jsonPath("$.originalDuration").value("PT40M"))
                .andExpect(jsonPath("$.posts[0].title").value("미리보기 제목"))
                .andExpect(jsonPath("$.posts[0].comments[0].replies[0].content").value("답글"));
    }

    @DisplayName("POST /admin/dummy/sql-insert/{scenarioId}: startAt과 duration을 받아 적재한다.")
    @Test
    void insertsWithStartAtAndDuration() throws Exception {
        // given
        givenAdminAuthentication();
        OffsetDateTime startAt = OffsetDateTime.parse("2026-05-04T17:30:00+09:00");
        Duration duration = Duration.ofMinutes(90);
        given(service.insert(eq(1L), any(OffsetDateTime.class), any(Duration.class))).willReturn(new DummySqlInsertResponse(
                1L,
                "my-scenario.yml",
                1,
                1,
                1,
                "INSERTED",
                startAt,
                duration
        ));

        // when // then
        mockMvc.perform(post("/admin/dummy/sql-insert/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "startAt": "2026-05-04T17:30:00+09:00",
                                  "duration": "PT1H30M"
                                }
                                """)
                        .cookie(new Cookie("accessToken", ACCESS_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appliedStartAt").value("2026-05-04T17:30:00+09:00"))
                .andExpect(jsonPath("$.appliedDuration").value("PT1H30M"));
        verify(service).insert(1L, startAt, duration);
    }

    @DisplayName("POST /admin/dummy/sql-insert/upload: multipart YAML 업로드를 service.upload에 위임하고 상태를 반환한다.")
    @Test
    void uploadsScenarioFile() throws Exception {
        // given
        givenAdminAuthentication();
        given(service.upload(any(MultipartFile.class))).willReturn(new DummySqlInsertStatusResponse(
                5L,
                "my-scenario.yml",
                "UPLOADED",
                OffsetDateTime.parse("2026-05-04T14:00:00+09:00"),
                null,
                null,
                Duration.ofMinutes(40),
                null,
                1,
                0
        ));
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "my-scenario.yml",
                "text/yaml",
                "scenarios: []".getBytes(StandardCharsets.UTF_8)
        );

        // when // then
        mockMvc.perform(multipart("/admin/dummy/sql-insert/upload")
                        .file(file)
                        .cookie(new Cookie("accessToken", ACCESS_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scenarioId").value(5))
                .andExpect(jsonPath("$.originalFilename").value("my-scenario.yml"))
                .andExpect(jsonPath("$.status").value("UPLOADED"))
                .andExpect(jsonPath("$.originalDuration").value("PT40M"));
        verify(service).upload(any(MultipartFile.class));
    }

    private void givenAdminAuthentication() {
        given(jwtExtractor.extractTokenFromCookie(eq("accessToken"), any(Cookie[].class))).willReturn(ACCESS_TOKEN);
        given(jwtProvider.extractTokenPayload(ACCESS_TOKEN))
                .willReturn(new TokenPayload(1L, MemberRole.ADMIN.name()));
    }
}
