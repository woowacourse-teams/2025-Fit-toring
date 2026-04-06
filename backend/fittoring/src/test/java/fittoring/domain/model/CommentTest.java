package fittoring.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.application.FixtureUtil;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.MisMatchPasswordException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class CommentTest {

    @DisplayName("회원 루트 댓글을 생성한다.")
    @Test
    void createRootCommentForMember() {
        Member member = FixtureUtil.testMentee();
        Post post = FixtureUtil.testGuestPost();

        Comment actual = Comment.forMember(post, member, "comment", true, "anonymous", null, null);

        assertThat(actual)
                .extracting(Comment::getPost, Comment::getMember, Comment::getRootId, Comment::getParentId)
                .containsExactly(post, member, null, null);
        assertThat(actual.isGuestComment()).isFalse();
    }

    @DisplayName("회원 대댓글은 rootId와 parentId를 가진다.")
    @Test
    void createReplyCommentForMember() {
        Member member = FixtureUtil.testMentee();
        Post post = FixtureUtil.testGuestPost();

        Comment actual = Comment.forMember(post, member, "reply", false, "nickname", 1L, 2L);

        assertThat(actual)
                .extracting(Comment::getRootId, Comment::getParentId)
                .containsExactly(1L, 2L);
    }

    @DisplayName("비회원 댓글을 생성한다.")
    @Test
    void createGuestComment() {
        Post post = FixtureUtil.testGuestPost();

        Comment actual = Comment.forGuest(post, "comment", "guest", "1234", null, null);

        assertThat(actual.getMember()).isNull();
        assertThat(actual.getGuestPassword()).isNotBlank();
        assertThat(actual.isGuestComment()).isTrue();
    }

    @DisplayName("댓글 내용을 수정한다.")
    @Test
    void modify() {
        Post post = FixtureUtil.testGuestPost();
        Comment comment = Comment.forGuest(post, "comment", "guest", "1234", null, null);

        comment.modify("updated comment");

        assertThat(comment.getContent()).isEqualTo("updated comment");
    }

    @DisplayName("회원 댓글의 작성자 여부를 확인한다.")
    @Test
    void isOwnedBy() {
        Member member = FixtureUtil.testMentee();
        ReflectionTestUtils.setField(member, "id", 1L);
        Post post = FixtureUtil.testGuestPost();
        Comment comment = Comment.forMember(post, member, "comment", false, "nickname", null, null);

        assertThat(comment.isOwnedBy(1L)).isTrue();
        assertThat(comment.isOwnedBy(2L)).isFalse();
    }

    @DisplayName("비회원 댓글의 비밀번호가 일치하면 통과한다.")
    @Test
    void matchGuestPassword() {
        Post post = FixtureUtil.testGuestPost();
        Comment comment = Comment.forGuest(post, "comment", "guest", "1234", null, null);

        assertThatCode(() -> comment.matchGuestPassword("1234"))
                .doesNotThrowAnyException();
    }

    @DisplayName("비회원 댓글의 비밀번호가 일치하지 않으면 예외가 발생한다.")
    @Test
    void failGuestPassword() {
        Post post = FixtureUtil.testGuestPost();
        Comment comment = Comment.forGuest(post, "comment", "guest", "1234", null, null);

        assertThatThrownBy(() -> comment.matchGuestPassword("9999"))
                .isInstanceOf(MisMatchPasswordException.class)
                .hasMessage(BusinessErrorMessage.GUEST_PASSWORD_MISMATCH.getMessage());
    }

    @DisplayName("비회원 댓글의 비밀번호가 null 또는 공백이면 예외가 발생한다.")
    @Test
    void failGuestPasswordWhenBlank() {
        Post post = FixtureUtil.testGuestPost();
        Comment comment = Comment.forGuest(post, "comment", "guest", "1234", null, null);

        assertThatThrownBy(() -> comment.matchGuestPassword(" "))
                .isInstanceOf(MisMatchPasswordException.class)
                .hasMessage(BusinessErrorMessage.GUEST_PASSWORD_MISMATCH.getMessage());
        assertThatThrownBy(() -> comment.matchGuestPassword(null))
                .isInstanceOf(MisMatchPasswordException.class)
                .hasMessage(BusinessErrorMessage.GUEST_PASSWORD_MISMATCH.getMessage());
    }

    @DisplayName("댓글이 특정 게시글에 속하는지 확인한다.")
    @Test
    void belongsTo() {
        Post post = FixtureUtil.testGuestPost();
        ReflectionTestUtils.setField(post, "id", 1L);
        Comment comment = Comment.forGuest(post, "comment", "guest", "1234", null, null);

        assertThat(comment.belongsTo(1L)).isTrue();
        assertThat(comment.belongsTo(2L)).isFalse();
    }
}
