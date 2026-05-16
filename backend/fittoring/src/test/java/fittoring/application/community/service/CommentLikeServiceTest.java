package fittoring.application.community.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import fittoring.IntegrationTestSupport;
import fittoring.application.FixtureUtil;
import fittoring.application.community.presentation.dto.response.CommentLikeResponse;
import fittoring.application.community.repository.CommentRepository;
import fittoring.application.community.repository.PostRepository;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ForbiddenException;
import fittoring.domain.model.Comment;
import fittoring.domain.model.LikeActorKeyHash;
import fittoring.domain.model.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentLikeServiceTest extends IntegrationTestSupport {

    private static final LikeActorKeyHash ACTOR_1 = new LikeActorKeyHash("a".repeat(64));
    private static final LikeActorKeyHash ACTOR_2 = new LikeActorKeyHash("b".repeat(64));

    @Autowired
    private CommentLikeService commentLikeService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @DisplayName("같은 actor로 댓글 좋아요를 반복하면 한 번만 반영된다.")
    @Test
    void likeCommentNoOpWhenSameActorLikesAgain() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(FixtureUtil.testGuestComment(post));

        // when
        CommentLikeResponse first = commentLikeService.like(post.getId(), comment.getId(), ACTOR_1);
        CommentLikeResponse second = commentLikeService.like(post.getId(), comment.getId(), ACTOR_1);

        // then
        Comment actual = commentRepository.findById(comment.getId()).orElseThrow();
        assertSoftly(softly -> {
            softly.assertThat(first.commentId()).isEqualTo(comment.getId());
            softly.assertThat(first.liked()).isTrue();
            softly.assertThat(first.likeCount()).isEqualTo(1);
            softly.assertThat(second.liked()).isTrue();
            softly.assertThat(second.likeCount()).isEqualTo(1);
            softly.assertThat(actual.getLikeCount()).isEqualTo(1);
        });
    }

    @DisplayName("다른 actor로 댓글 좋아요를 누르면 각각 반영된다.")
    @Test
    void likeCommentWhenDifferentActorsLike() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(FixtureUtil.testGuestComment(post));

        // when
        CommentLikeResponse first = commentLikeService.like(post.getId(), comment.getId(), ACTOR_1);
        CommentLikeResponse second = commentLikeService.like(post.getId(), comment.getId(), ACTOR_2);

        // then
        Comment actual = commentRepository.findById(comment.getId()).orElseThrow();
        assertSoftly(softly -> {
            softly.assertThat(first.likeCount()).isEqualTo(1);
            softly.assertThat(second.likeCount()).isEqualTo(2);
            softly.assertThat(actual.getLikeCount()).isEqualTo(2);
        });
    }

    @DisplayName("대댓글에도 좋아요를 누를 수 있다.")
    @Test
    void likeReplyComment() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment root = commentRepository.save(FixtureUtil.testGuestComment(post, "root"));
        Comment reply = commentRepository.save(FixtureUtil.testGuestReplyComment(post, root.getId(), root.getId()));

        // when
        CommentLikeResponse actual = commentLikeService.like(post.getId(), reply.getId(), ACTOR_1);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual.commentId()).isEqualTo(reply.getId());
            softly.assertThat(actual.liked()).isTrue();
            softly.assertThat(actual.likeCount()).isEqualTo(1);
        });
    }

    @DisplayName("같은 actor로 댓글 좋아요 취소를 반복하면 한 번만 반영된다.")
    @Test
    void unlikeCommentNoOpWhenSameActorUnlikesAgain() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(FixtureUtil.testGuestComment(post));
        commentLikeService.like(post.getId(), comment.getId(), ACTOR_1);

        // when
        CommentLikeResponse first = commentLikeService.unlike(post.getId(), comment.getId(), ACTOR_1);
        CommentLikeResponse second = commentLikeService.unlike(post.getId(), comment.getId(), ACTOR_1);

        // then
        Comment actual = commentRepository.findById(comment.getId()).orElseThrow();
        assertSoftly(softly -> {
            softly.assertThat(first.liked()).isFalse();
            softly.assertThat(first.likeCount()).isZero();
            softly.assertThat(second.liked()).isFalse();
            softly.assertThat(second.likeCount()).isZero();
            softly.assertThat(actual.getLikeCount()).isZero();
        });
    }

    @DisplayName("식별 쿠키가 없으면 좋아요 취소는 기존 좋아요를 변경하지 않는다.")
    @Test
    void unlikeCommentNoOpWhenActorKeyHashIsNull() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(FixtureUtil.testGuestComment(post));
        commentLikeService.like(post.getId(), comment.getId(), ACTOR_1);

        // when
        CommentLikeResponse actual = commentLikeService.unlike(post.getId(), comment.getId(), null);

        // then
        Comment saved = commentRepository.findById(comment.getId()).orElseThrow();
        assertSoftly(softly -> {
            softly.assertThat(actual.liked()).isFalse();
            softly.assertThat(actual.likeCount()).isEqualTo(1);
            softly.assertThat(saved.getLikeCount()).isEqualTo(1);
        });
    }

    @DisplayName("식별 해시가 없으면 댓글 좋아요는 예외가 발생한다.")
    @Test
    void likeFailsWhenActorKeyHashIsNull() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(FixtureUtil.testGuestComment(post));

        // when // then
        assertThatThrownBy(() -> commentLikeService.like(post.getId(), comment.getId(), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("actorKeyHash는 null일 수 없습니다.");
    }

    @DisplayName("댓글이 게시글에 속하지 않으면 좋아요를 누를 수 없다.")
    @Test
    void likeFailsWhenCommentDoesNotBelongToPost() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Post otherPost = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(FixtureUtil.testGuestComment(otherPost));

        // when // then
        assertThatThrownBy(() -> commentLikeService.like(post.getId(), comment.getId(), ACTOR_1))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(BusinessErrorMessage.COMMENT_NOT_BELONG_TO_POST.getMessage());
    }
}
