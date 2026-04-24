package fittoring.application.community.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.application.auth.CookieProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostLikeActorResolverTest {

    private static final CookieProvider COOKIE_PROVIDER = new CookieProvider("None");

    @DisplayName("actor secret이 유효하면 PostLikeActorResolver를 생성한다.")
    @Test
    void constructWithValidActorSecret() {
        // when // then
        assertThatCode(() -> new PostLikeActorResolver(COOKIE_PROVIDER, "post-like-actor-secret"))
                .doesNotThrowAnyException();
    }

    @DisplayName("actor secret이 null이면 예외가 발생한다.")
    @Test
    void constructWithNullActorSecret() {
        // when // then
        assertThatThrownBy(() -> new PostLikeActorResolver(COOKIE_PROVIDER, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("post-like.actor-secret 설정은 비어 있을 수 없습니다.");
    }

    @DisplayName("actor secret이 공백이면 예외가 발생한다.")
    @Test
    void constructWithBlankActorSecret() {
        // when // then
        assertThatThrownBy(() -> new PostLikeActorResolver(COOKIE_PROVIDER, " "))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("post-like.actor-secret 설정은 비어 있을 수 없습니다.");
    }
}
