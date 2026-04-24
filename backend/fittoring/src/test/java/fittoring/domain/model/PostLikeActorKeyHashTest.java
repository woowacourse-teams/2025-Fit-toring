package fittoring.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.application.exception.InvalidPostLikeActorKeyHashException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostLikeActorKeyHashTest {

    @DisplayName("64자리 소문자 hex 문자열이면 PostLikeActorKeyHash를 생성한다.")
    @Test
    void constructWithValidHash() {
        // given
        String value = "a".repeat(64);

        // when
        PostLikeActorKeyHash actual = new PostLikeActorKeyHash(value);

        // then
        assertThat(actual.getValue()).isEqualTo(value);
    }

    @DisplayName("64자리 소문자 hex 문자열이 아니면 예외가 발생한다.")
    @Test
    void constructWithInvalidHash() {
        // given
        String value = "invalid-hash";

        // when // then
        assertThatThrownBy(() -> new PostLikeActorKeyHash(value))
                .isInstanceOf(InvalidPostLikeActorKeyHashException.class)
                .hasMessage("올바르지 않은 게시글 좋아요 식별자 해시입니다.");
    }
}
