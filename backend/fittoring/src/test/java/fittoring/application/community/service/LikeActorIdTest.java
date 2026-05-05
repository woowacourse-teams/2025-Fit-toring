package fittoring.application.community.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.application.exception.InvalidLikeActorIdException;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LikeActorIdTest {

    @DisplayName("유효한 UUID 문자열이면 LikeActorId를 생성한다.")
    @Test
    void fromValidUuid() {
        // given
        String value = UUID.randomUUID().toString();

        // when
        Optional<LikeActorId> actual = LikeActorId.from(value);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actual).isPresent();
            softly.assertThat(actual.get().value()).isEqualTo(value);
        });
    }

    @DisplayName("유효하지 않은 UUID 문자열이면 LikeActorId를 생성하지 않는다.")
    @Test
    void fromInvalidUuid() {
        // given
        String value = "invalid-uuid";

        // when
        Optional<LikeActorId> actual = LikeActorId.from(value);

        // then
        assertThat(actual).isEmpty();
    }

    @DisplayName("비정규 UUID 문자열이면 LikeActorId를 생성하지 않는다.")
    @Test
    void fromNonCanonicalUuid() {
        // given
        String value = "0-0-0-0-0";

        // when
        Optional<LikeActorId> actual = LikeActorId.from(value);

        // then
        assertThat(actual).isEmpty();
    }

    @DisplayName("생성자로 유효하지 않은 UUID 문자열을 전달하면 예외가 발생한다.")
    @Test
    void constructWithInvalidUuid() {
        // given
        String value = "invalid-uuid";

        // when // then
        assertThatThrownBy(() -> new LikeActorId(value))
                .isInstanceOf(InvalidLikeActorIdException.class)
                .hasMessage("올바르지 않은 좋아요 식별자입니다.");
    }

    @DisplayName("새 LikeActorId를 생성한다.")
    @Test
    void create() {
        // given // when
        LikeActorId actual = LikeActorId.create();

        // then
        assertThat(UUID.fromString(actual.value())).isNotNull();
    }
}
