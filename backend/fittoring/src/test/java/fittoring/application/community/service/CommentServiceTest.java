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
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.MisMatchPasswordException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.Comment;
import fittoring.domain.model.Member;
import fittoring.domain.model.Post;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentServiceTest extends IntegrationTestSupport {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

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
        Comment root = commentRepository.save(Comment.forGuest(post, "root", "guest", "1234", null, null));
        Comment parent = commentRepository.save(Comment.forGuest(post, "parent", "guest", "1234", root.getId(), root.getId()));

        CommentResponse actual = commentService.createComment(
                new CommentCreateDto(null, post.getId(), "reply", false, "guest", "1234", root.getId(), parent.getId()));

        assertSoftly(softly -> {
            softly.assertThat(actual.isGuestComment()).isTrue();
            softly.assertThat(actual.rootId()).isEqualTo(root.getId());
            softly.assertThat(actual.parentId()).isEqualTo(parent.getId());
        });
    }

    @DisplayName("게시글 댓글 목록을 조회한다.")
    @Test
    void findComments() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        commentRepository.save(Comment.forGuest(post, "comment-1", "guest", "1234", null, null));
        commentRepository.save(Comment.forGuest(post, "comment-2", "guest", "1234", null, null));

        List<CommentResponse> actual = commentService.findComments(post.getId());

        assertThat(actual).hasSize(2);
    }

    @DisplayName("회원 댓글을 수정한다.")
    @Test
    void modifyMemberComment() {
        Member member = memberRepository.save(FixtureUtil.testMentee());
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(Comment.forMember(post, member, "old", false, member.getName(), null, null));

        commentService.modifyComment(new CommentUpdateDto(member.getId(), comment.getId(), "new", null));

        assertThat(commentRepository.findById(comment.getId()).orElseThrow().getContent()).isEqualTo("new");
    }

    @DisplayName("비회원 댓글을 비밀번호로 수정한다.")
    @Test
    void modifyGuestComment() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(Comment.forGuest(post, "old", "guest", "1234", null, null));

        commentService.modifyComment(new CommentUpdateDto(null, comment.getId(), "new", "1234"));

        assertThat(commentRepository.findById(comment.getId()).orElseThrow().getContent()).isEqualTo("new");
    }

    @DisplayName("다른 회원은 댓글을 수정할 수 없다.")
    @Test
    void modifyMemberCommentForbidden() {
        Member owner = memberRepository.save(FixtureUtil.testMentee());
        Member other = memberRepository.save(FixtureUtil.testMentee(2));
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(Comment.forMember(post, owner, "old", false, owner.getName(), null, null));

        assertThatThrownBy(() -> commentService.modifyComment(new CommentUpdateDto(other.getId(), comment.getId(), "new", null)))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
    }

    @DisplayName("비회원 댓글은 비밀번호가 틀리면 수정할 수 없다.")
    @Test
    void modifyGuestCommentFail() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(Comment.forGuest(post, "old", "guest", "1234", null, null));

        assertThatThrownBy(() -> commentService.modifyComment(new CommentUpdateDto(null, comment.getId(), "new", "9999")))
                .isInstanceOf(MisMatchPasswordException.class)
                .hasMessage(BusinessErrorMessage.GUEST_PASSWORD_MISMATCH.getMessage());
    }

    @DisplayName("댓글을 삭제하면 soft delete 된다.")
    @Test
    void deleteComment() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(Comment.forGuest(post, "old", "guest", "1234", null, null));

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
