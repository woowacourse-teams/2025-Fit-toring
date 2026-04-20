package fittoring.application.community.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import fittoring.IntegrationTestSupport;
import fittoring.application.FixtureUtil;
import fittoring.application.community.presentation.dto.response.PostLikeResponse;
import fittoring.application.community.repository.PostRepository;
import fittoring.domain.model.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PostLikeServiceTest extends IntegrationTestSupport {

    private static final String ACTOR_1 = "a".repeat(64);
    private static final String ACTOR_2 = "b".repeat(64);

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private PostRepository postRepository;

    @DisplayName("같은 postLikeActorId로 게시글 좋아요를 반복하면 한 번만 반영된다.")
    @Test
    void likePostNoOpWhenSameActorLikesAgain() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());

        // when
        PostLikeResponse first = postLikeService.like(post.getId(), ACTOR_1);
        PostLikeResponse second = postLikeService.like(post.getId(), ACTOR_1);

        // then
        Post actual = postRepository.findById(post.getId()).orElseThrow();
        assertSoftly(softly -> {
            softly.assertThat(first.postId()).isEqualTo(post.getId());
            softly.assertThat(first.liked()).isTrue();
            softly.assertThat(first.likeCount()).isEqualTo(1);
            softly.assertThat(second.liked()).isTrue();
            softly.assertThat(second.likeCount()).isEqualTo(1);
            softly.assertThat(actual.getLikeCount()).isEqualTo(1);
        });
    }

    @DisplayName("같은 postLikeActorId로 게시글 좋아요 취소를 반복하면 한 번만 반영된다.")
    @Test
    void unlikePostNoOpWhenSameActorUnlikesAgain() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        postLikeService.like(post.getId(), ACTOR_1);

        // when
        PostLikeResponse first = postLikeService.unlike(post.getId(), ACTOR_1);
        PostLikeResponse second = postLikeService.unlike(post.getId(), ACTOR_1);

        // then
        Post actual = postRepository.findById(post.getId()).orElseThrow();
        assertSoftly(softly -> {
            softly.assertThat(first.postId()).isEqualTo(post.getId());
            softly.assertThat(first.liked()).isFalse();
            softly.assertThat(first.likeCount()).isZero();
            softly.assertThat(second.liked()).isFalse();
            softly.assertThat(second.likeCount()).isZero();
            softly.assertThat(actual.getLikeCount()).isZero();
        });
    }

    @DisplayName("식별 쿠키가 없으면 좋아요 취소는 기존 좋아요를 변경하지 않는다.")
    @Test
    void unlikePostNoOpWhenActorKeyHashIsNull() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        postLikeService.like(post.getId(), ACTOR_2);

        // when
        PostLikeResponse actual = postLikeService.unlike(post.getId(), null);

        // then
        Post saved = postRepository.findById(post.getId()).orElseThrow();
        assertSoftly(softly -> {
            softly.assertThat(actual.postId()).isEqualTo(post.getId());
            softly.assertThat(actual.liked()).isFalse();
            softly.assertThat(actual.likeCount()).isEqualTo(1);
            softly.assertThat(saved.getLikeCount()).isEqualTo(1);
        });
    }
}
