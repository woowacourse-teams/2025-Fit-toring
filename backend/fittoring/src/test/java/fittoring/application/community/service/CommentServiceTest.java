package fittoring.application.community.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import fittoring.IntegrationTestSupport;
import fittoring.application.FixtureUtil;
import fittoring.application.community.presentation.dto.response.CommentResponse;
import fittoring.application.community.repository.CommentRepository;
import fittoring.application.community.repository.PostRepository;
import fittoring.application.community.service.dto.CommentCreateDto;
import fittoring.application.community.service.dto.CommentDeleteDto;
import fittoring.application.community.service.dto.CommentUpdateDto;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.CommentNotFoundException;
import fittoring.application.exception.EmptyRequestException;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.InvalidCommentReplyException;
import fittoring.application.exception.MisMatchPasswordException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.Comment;
import fittoring.domain.model.LikeActorKeyHash;
import fittoring.domain.model.Member;
import fittoring.domain.model.Post;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentServiceTest extends IntegrationTestSupport {

    private static final LikeActorKeyHash ACTOR_1 = new LikeActorKeyHash("a".repeat(64));
    private static final LikeActorKeyHash ACTOR_2 = new LikeActorKeyHash("b".repeat(64));

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommentLikeService commentLikeService;

    @DisplayName("회원 루트 댓글을 생성한다.")
    @Test
    void createMemberRootComment() {
        Member member = memberRepository.save(FixtureUtil.testMentee());
        Post post = postRepository.save(FixtureUtil.testGuestPost());

        CommentResponse actual = commentService.createComment(
                new CommentCreateDto(member.getId(), post.getId(), "content", false, null, null, null, null));

        assertSoftly(softly -> {
            softly.assertThat(actual.content()).isEqualTo("content");
            softly.assertThat(actual.isGuestComment()).isFalse();
            softly.assertThat(actual.rootId()).isNull();
        });
    }

    @DisplayName("비회원 대댓글을 생성한다.")
    @Test
    void createGuestReplyComment() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment root = commentRepository.save(FixtureUtil.testGuestComment(post, "root"));
        Comment parent = commentRepository.save(FixtureUtil.testGuestReplyComment(post, root.getId(), root.getId()));

        CommentResponse actual = commentService.createComment(
                new CommentCreateDto(null, post.getId(), "reply", false, "guest", "1234", root.getId(), parent.getId()));

        assertSoftly(softly -> {
            softly.assertThat(actual.isGuestComment()).isTrue();
            softly.assertThat(actual.rootId()).isEqualTo(root.getId());
            softly.assertThat(actual.parentId()).isEqualTo(parent.getId());
        });
    }

    @DisplayName("비회원 댓글 생성 시 닉네임과 비밀번호는 필수다.")
    @Test
    void createGuestCommentFailWhenGuestFieldsAreBlank() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());

        assertThatThrownBy(() -> commentService.createComment(
                new CommentCreateDto(null, post.getId(), "content", false, "", "1234", null, null)))
                .isInstanceOf(EmptyRequestException.class)
                .hasMessage(BusinessErrorMessage.GUEST_NICKNAME_REQUIRED.getMessage());

        assertThatThrownBy(() -> commentService.createComment(
                new CommentCreateDto(null, post.getId(), "content", false, "guest", "", null, null)))
                .isInstanceOf(EmptyRequestException.class)
                .hasMessage(BusinessErrorMessage.GUEST_PASSWORD_REQUIRED.getMessage());
    }

    @DisplayName("대댓글 생성 시 rootId와 parentId 중 하나만 있으면 예외가 발생한다.")
    @Test
    void createReplyCommentFailWhenReplyIdsAreIncomplete() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment root = commentRepository.save(FixtureUtil.testGuestComment(post, "root"));

        assertThatThrownBy(() -> commentService.createComment(
                new CommentCreateDto(null, post.getId(), "reply", false, "guest", "1234", root.getId(), null)))
                .isInstanceOf(InvalidCommentReplyException.class)
                .hasMessage(BusinessErrorMessage.INVALID_COMMENT_REPLY.getMessage());
    }

    @DisplayName("게시글 댓글 목록을 조회한다.")
    @Test
    void findComments() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        commentRepository.save(FixtureUtil.testGuestComment(post, "comment-1"));
        commentRepository.save(FixtureUtil.testGuestComment(post, "comment-2"));

        List<CommentResponse> actual = commentService.findComments(post.getId());

        assertThat(actual).hasSize(2);
    }

    @DisplayName("댓글 목록 조회 시 각 댓글의 좋아요 수와 liked 여부가 포함된다.")
    @Test
    void findCommentsWithLikeInfo() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment likedComment = commentRepository.save(FixtureUtil.testGuestComment(post, "comment-1"));
        Comment notLikedComment = commentRepository.save(FixtureUtil.testGuestComment(post, "comment-2"));
        commentLikeService.like(post.getId(), likedComment.getId(), ACTOR_1);

        // when
        List<CommentResponse> likedResponses = commentService.findComments(post.getId(), ACTOR_1);
        List<CommentResponse> notLikedResponses = commentService.findComments(post.getId(), ACTOR_2);

        // then
        CommentResponse liked = likedResponses.stream()
                .filter(response -> response.id().equals(likedComment.getId()))
                .findFirst()
                .orElseThrow();
        CommentResponse notLiked = likedResponses.stream()
                .filter(response -> response.id().equals(notLikedComment.getId()))
                .findFirst()
                .orElseThrow();
        CommentResponse otherActor = notLikedResponses.stream()
                .filter(response -> response.id().equals(likedComment.getId()))
                .findFirst()
                .orElseThrow();
        assertSoftly(softly -> {
            softly.assertThat(liked.likeCount()).isEqualTo(1);
            softly.assertThat(liked.liked()).isTrue();
            softly.assertThat(notLiked.likeCount()).isZero();
            softly.assertThat(notLiked.liked()).isFalse();
            softly.assertThat(otherActor.likeCount()).isEqualTo(1);
            softly.assertThat(otherActor.liked()).isFalse();
        });
    }

    @DisplayName("대댓글 생성 시 rootId가 루트 댓글이 아니면 예외가 발생한다.")
    @Test
    void createReplyCommentFailWhenRootIdIsNotRootComment() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment root = commentRepository.save(FixtureUtil.testGuestComment(post, "root"));
        Comment reply = commentRepository.save(FixtureUtil.testGuestReplyComment(post, root.getId(), root.getId()));

        assertThatThrownBy(() -> commentService.createComment(
                new CommentCreateDto(null, post.getId(), "reply", false, "guest", "1234", reply.getId(), root.getId())))
                .isInstanceOf(InvalidCommentReplyException.class)
                .hasMessage(BusinessErrorMessage.INVALID_COMMENT_REPLY.getMessage());
    }

    @DisplayName("대댓글 생성 시 parentId가 rootId 하위 댓글이 아니면 예외가 발생한다.")
    @Test
    void createReplyCommentFailWhenParentDoesNotBelongToRoot() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment root = commentRepository.save(FixtureUtil.testGuestComment(post, "root"));
        Comment otherRoot = commentRepository.save(FixtureUtil.testGuestComment(post, "other-root"));

        assertThatThrownBy(() -> commentService.createComment(
                new CommentCreateDto(null, post.getId(), "reply", false, "guest", "1234", root.getId(), otherRoot.getId())))
                .isInstanceOf(InvalidCommentReplyException.class)
                .hasMessage(BusinessErrorMessage.INVALID_COMMENT_REPLY.getMessage());
    }

    @DisplayName("회원 댓글을 수정한다.")
    @Test
    void modifyMemberComment() {
        Member member = memberRepository.save(FixtureUtil.testMentee());
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(FixtureUtil.testMemberComment(post, member, "old"));

        commentService.modifyComment(new CommentUpdateDto(member.getId(), comment.getId(), "new", null));

        assertThat(commentRepository.findById(comment.getId()).orElseThrow().getContent()).isEqualTo("new");
    }

    @DisplayName("비회원 댓글을 비밀번호로 수정한다.")
    @Test
    void modifyGuestComment() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(FixtureUtil.testGuestComment(post, "old"));

        commentService.modifyComment(new CommentUpdateDto(null, comment.getId(), "new", "1234"));

        assertThat(commentRepository.findById(comment.getId()).orElseThrow().getContent()).isEqualTo("new");
    }

    @DisplayName("다른 회원은 댓글을 수정할 수 없다.")
    @Test
    void modifyMemberCommentForbidden() {
        Member owner = memberRepository.save(FixtureUtil.testMentee());
        Member other = memberRepository.save(FixtureUtil.testMentee(2));
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(FixtureUtil.testMemberComment(post, owner, "old"));

        assertThatThrownBy(() -> commentService.modifyComment(new CommentUpdateDto(other.getId(), comment.getId(), "new", null)))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
    }

    @DisplayName("비회원 댓글은 비밀번호가 틀리면 수정할 수 없다.")
    @Test
    void modifyGuestCommentFail() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(FixtureUtil.testGuestComment(post, "old"));

        assertThatThrownBy(() -> commentService.modifyComment(new CommentUpdateDto(null, comment.getId(), "new", "9999")))
                .isInstanceOf(MisMatchPasswordException.class)
                .hasMessage(BusinessErrorMessage.GUEST_PASSWORD_MISMATCH.getMessage());
    }

    @DisplayName("비회원 댓글 비밀번호를 검증한다.")
    @Test
    void validateGuestPassword() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(FixtureUtil.testGuestComment(post));

        // when & then
        commentService.validateGuestPassword(comment.getId(), "1234");
    }

    @DisplayName("비회원 댓글 비밀번호가 틀리면 예외가 발생한다.")
    @Test
    void validateGuestPasswordFail() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(FixtureUtil.testGuestComment(post));

        // when & then
        assertThatThrownBy(() -> commentService.validateGuestPassword(comment.getId(), "9999"))
                .isInstanceOf(MisMatchPasswordException.class)
                .hasMessage(BusinessErrorMessage.GUEST_PASSWORD_MISMATCH.getMessage());
    }

    @DisplayName("댓글을 삭제하면 soft delete 된다.")
    @Test
    void deleteComment() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(FixtureUtil.testGuestComment(post, "old"));

        commentService.deleteComment(new CommentDeleteDto(null, comment.getId(), "1234"));

        assertThat(commentRepository.findById(comment.getId())).isEmpty();
        assertThat(commentRepository.findDeletedById(comment.getId()).isDeleted()).isTrue();
    }

    @DisplayName("없는 댓글을 수정하면 예외가 발생한다.")
    @Test
    void modifyCommentFailNotFound() {
        assertThatThrownBy(() -> commentService.modifyComment(new CommentUpdateDto(null, 999L, "new", "1234")))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessage(BusinessErrorMessage.COMMENT_NOT_FOUND.getMessage());
    }
}
