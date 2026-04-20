package fittoring.application.community.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.application.exception.InvalidPostLikeActorIdException;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostLikeActorIdTest {

    @DisplayName("유효한 UUID 문자열이면 PostLikeActorId를 생성한다.")
    @Test
    void fromValidUuid() {
        // given
        String value = UUID.randomUUID().toString();

        // when
        Optional<PostLikeActorId> actual = PostLikeActorId.from(value);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actual).isPresent();
            softly.assertThat(actual.get().value()).isEqualTo(value);
        });
    }

    @DisplayName("유효하지 않은 UUID 문자열이면 PostLikeActorId를 생성하지 않는다.")
    @Test
    void fromInvalidUuid() {
        // given
        String value = "invalid-uuid";

        // when
        Optional<PostLikeActorId> actual = PostLikeActorId.from(value);

        // then
        assertThat(actual).isEmpty();
    }

    @DisplayName("생성자로 유효하지 않은 UUID 문자열을 전달하면 예외가 발생한다.")
    @Test
    void constructWithInvalidUuid() {
        // given
        String value = "invalid-uuid";

        // when // then
        assertThatThrownBy(() -> new PostLikeActorId(value))
                .isInstanceOf(InvalidPostLikeActorIdException.class)
                .hasMessage("올바르지 않은 게시글 좋아요 식별자입니다.");
    }

    @DisplayName("새 PostLikeActorId를 생성한다.")
    @Test
    void create() {
        // given // when
        PostLikeActorId actual = PostLikeActorId.create();

        // then
        assertThat(UUID.fromString(actual.value())).isNotNull();
    }
}
