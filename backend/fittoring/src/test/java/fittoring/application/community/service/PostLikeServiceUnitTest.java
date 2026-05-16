package fittoring.application.community.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import fittoring.application.community.repository.PostLikeRepository;
import fittoring.application.community.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostLikeServiceUnitTest {

    private final PostRepository postRepository = mock(PostRepository.class);
    private final PostLikeRepository postLikeRepository = mock(PostLikeRepository.class);
    private final PostLikeService postLikeService = new PostLikeService(postRepository, postLikeRepository);

    @DisplayName("식별 해시가 없으면 게시글 좋아요는 예외가 발생한다.")
    @Test
    void likeFailsWhenActorKeyHashIsNull() {
        // when // then
        assertThatThrownBy(() -> postLikeService.like(1L, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("actorKeyHash는 null일 수 없습니다.");
        verifyNoInteractions(postRepository, postLikeRepository);
    }
}
