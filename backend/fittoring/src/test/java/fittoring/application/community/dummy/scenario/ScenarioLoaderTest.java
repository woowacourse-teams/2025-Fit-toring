package fittoring.application.community.dummy.scenario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ScenarioLoaderTest {

    private final ScenarioLoader loader = new ScenarioLoader();

    @DisplayName("정상 yml을 ScenarioFile로 파싱한다.")
    @Test
    void loadsValidYaml() {
        // given
        String yaml = """
                scenarios:
                  - post:
                      nickname: "글쓴이"
                      scheduled_at: "2026-05-04T15:00:00+09:00"
                      title: "샘플 제목"
                      content: "샘플 본문"
                    comments:
                      - nickname: "댓글러"
                        scheduled_at: "2026-05-04T15:05:00+09:00"
                        content: "루트 댓글"
                        replies:
                          - nickname: "대댓글러"
                            scheduled_at: "2026-05-04T15:10:00+09:00"
                            content: "대댓글"
                """;

        // when
        ScenarioFile result = loader.load(toStream(yaml));

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.scenarios()).hasSize(1);
            Scenario scenario = result.scenarios().get(0);
            ScenarioPost post = scenario.post();
            softly.assertThat(post.nickname()).isEqualTo("글쓴이");
            softly.assertThat(post.title()).isEqualTo("샘플 제목");
            softly.assertThat(post.content()).isEqualTo("샘플 본문");
            softly.assertThat(post.scheduledAt())
                    .isEqualTo(OffsetDateTime.parse("2026-05-04T15:00:00+09:00"));
            softly.assertThat(scenario.comments()).hasSize(1);
            ScenarioComment root = scenario.comments().get(0);
            softly.assertThat(root.nickname()).isEqualTo("댓글러");
            softly.assertThat(root.content()).isEqualTo("루트 댓글");
            softly.assertThat(root.scheduledAt())
                    .isEqualTo(OffsetDateTime.parse("2026-05-04T15:05:00+09:00"));
            softly.assertThat(root.replies()).hasSize(1);
            ScenarioComment reply = root.replies().get(0);
            softly.assertThat(reply.nickname()).isEqualTo("대댓글러");
            softly.assertThat(reply.content()).isEqualTo("대댓글");
            softly.assertThat(reply.scheduledAt())
                    .isEqualTo(OffsetDateTime.parse("2026-05-04T15:10:00+09:00"));
        });
    }

    @DisplayName("category 필드가 있어도 무시하고 파싱한다.")
    @Test
    void ignoresCategoryField() {
        // given
        String yaml = """
                scenarios:
                  - category: 식단
                    post:
                      nickname: "글쓴이"
                      scheduled_at: "2026-05-04T15:00:00+09:00"
                      title: "샘플"
                      content: "샘플"
                    comments: []
                """;

        // when
        ScenarioFile result = loader.load(toStream(yaml));

        // then
        assertThat(result.scenarios()).hasSize(1);
        assertThat(result.scenarios().get(0).comments()).isEmpty();
    }

    @DisplayName("scenarios가 비어 있으면 예외를 던진다.")
    @Test
    void rejectsEmptyScenarios() {
        // given
        String yaml = """
                scenarios: []
                """;

        // when // then
        assertThatThrownBy(() -> loader.load(toStream(yaml)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("scenarios 키가 없으면 예외를 던진다.")
    @Test
    void rejectsMissingScenarios() {
        // given
        String yaml = """
                title: wrong-root
                """;

        // when // then
        assertThatThrownBy(() -> loader.load(toStream(yaml)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("실제 scenarios1.yml을 파싱하고 검증을 통과한다.")
    @Test
    void loadsScenariosOne() throws IOException {
        // given
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("dummy/scenarios1.yml")) {
            // when
            ScenarioFile result = loader.load(input);

            // then
            assertThat(result.scenarios()).isNotEmpty();
        }
    }

    private ByteArrayInputStream toStream(String yaml) {
        return new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));
    }
}
