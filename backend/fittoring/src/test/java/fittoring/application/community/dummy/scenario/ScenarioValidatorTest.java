package fittoring.application.community.dummy.scenario;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ScenarioValidatorTest {

    private static final OffsetDateTime POST_AT = OffsetDateTime.parse("2026-05-04T15:00:00+09:00");

    @DisplayName("정상 시나리오는 통과한다.")
    @Test
    void passesValidScenario() {
        // given
        ScenarioComment reply = leaf("답글러", POST_AT.plusMinutes(10));
        ScenarioComment root = new ScenarioComment(
                "댓글러", POST_AT.plusMinutes(5), "루트", List.of(reply));
        ScenarioFile file = file(post(), List.of(root));

        // when // then
        assertThatCode(() -> ScenarioValidator.validate(file)).doesNotThrowAnyException();
    }

    @DisplayName("트리 깊이가 9를 초과하면 예외를 던진다.")
    @Test
    void rejectsDepthOverNine() {
        // given - 깊이 10 트리 (루트 1 + 자식 체인 9)
        ScenarioComment current = leaf("c9", POST_AT.plusMinutes(9));
        for (int i = 8; i >= 0; i--) {
            current = new ScenarioComment(
                    "c" + i, POST_AT.plusMinutes(i + 1), "x", List.of(current));
        }
        ScenarioFile file = file(post(), List.of(current));

        // when // then
        assertThatThrownBy(() -> ScenarioValidator.validate(file))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("트리 깊이가 9면 통과한다.")
    @Test
    void passesAtDepthNine() {
        // given - 깊이 9 트리
        ScenarioComment current = leaf("c8", POST_AT.plusMinutes(8));
        for (int i = 7; i >= 0; i--) {
            current = new ScenarioComment(
                    "c" + i, POST_AT.plusMinutes(i + 1), "x", List.of(current));
        }
        ScenarioFile file = file(post(), List.of(current));

        // when // then
        assertThatCode(() -> ScenarioValidator.validate(file)).doesNotThrowAnyException();
    }

    @DisplayName("댓글 scheduled_at이 부모 게시글보다 이르면 예외를 던진다.")
    @Test
    void rejectsCommentBeforePost() {
        // given
        ScenarioComment early = leaf("댓글러", POST_AT.minusMinutes(1));
        ScenarioFile file = file(post(), List.of(early));

        // when // then
        assertThatThrownBy(() -> ScenarioValidator.validate(file))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자식 댓글 scheduled_at이 직속 부모 댓글보다 이르면 예외를 던진다.")
    @Test
    void rejectsReplyBeforeParent() {
        // given
        ScenarioComment earlyReply = leaf("답글러", POST_AT.plusMinutes(5));
        ScenarioComment root = new ScenarioComment(
                "댓글러", POST_AT.plusMinutes(10), "루트", List.of(earlyReply));
        ScenarioFile file = file(post(), List.of(root));

        // when // then
        assertThatThrownBy(() -> ScenarioValidator.validate(file))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("게시글 nickname이 비어 있으면 예외를 던진다.")
    @Test
    void rejectsMissingPostNickname() {
        // given
        ScenarioPost noNickname = new ScenarioPost(null, POST_AT, "제목", "본문");
        ScenarioFile file = file(noNickname, List.of());

        // when // then
        assertThatThrownBy(() -> ScenarioValidator.validate(file))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("댓글 nickname이 비어 있으면 예외를 던진다.")
    @Test
    void rejectsMissingCommentNickname() {
        // given
        ScenarioComment noNickname = new ScenarioComment(
                null, POST_AT.plusMinutes(5), "x", List.of());
        ScenarioFile file = file(post(), List.of(noNickname));

        // when // then
        assertThatThrownBy(() -> ScenarioValidator.validate(file))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private ScenarioPost post() {
        return new ScenarioPost("글쓴이", POST_AT, "제목", "본문");
    }

    private ScenarioComment leaf(String nickname, OffsetDateTime at) {
        return new ScenarioComment(nickname, at, "x", List.of());
    }

    private ScenarioFile file(ScenarioPost post, List<ScenarioComment> comments) {
        return new ScenarioFile(List.of(new Scenario(post, comments)));
    }
}
