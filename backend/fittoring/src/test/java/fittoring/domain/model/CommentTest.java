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

    @DisplayName("회원 루트 댓글을 생성할 수 있다.")
    @Test
    void createRootCommentForMember() {
        //given
        Member member = FixtureUtil.testMentee();
        Post post = FixtureUtil.testGuestPost();

        //when
        Comment actual = Comment.forMember(post, member, "댓글", true, "익명", null, null);

        //then
        assertThat(actual)
                .extracting(Comment::getPost, Comment::getMember, Comment::getRootId, Comment::getParentId)
                .containsExactly(post, member, null, null);
        assertThat(actual.isGuestComment()).isFalse();
    }

    @DisplayName("회원 대댓글은 rootId와 parentId를 가진다.")
    @Test
    void createReplyCommentForMember() {
        //given
        Member member = FixtureUtil.testMentee();
        Post post = FixtureUtil.testGuestPost();

        //when
        Comment actual = Comment.forMember(post, member, "대댓글", false, "닉네임", 1L, 2L);

        //then
        assertThat(actual)
                .extracting(Comment::getRootId, Comment::getParentId)
                .containsExactly(1L, 2L);
    }

    @DisplayName("비회원 댓글을 생성할 수 있다.")
    @Test
    void createGuestComment() {
        //given
        Post post = FixtureUtil.testGuestPost();

        //when
        Comment actual = Comment.forGuest(post, "댓글", "비회원", "1234", null, null);

        //then
        assertThat(actual.getMember()).isNull();
        assertThat(actual.getGuestPassword()).isNotBlank();
        assertThat(actual.isGuestComment()).isTrue();
    }

    @DisplayName("댓글 내용을 수정할 수 있다.")
    @Test
    void modify() {
        //given
        Post post = FixtureUtil.testGuestPost();
        Comment comment = Comment.forGuest(post, "댓글", "비회원", "1234", null, null);

        //when
        comment.modify("수정 댓글");

        //then
        assertThat(comment.getContent()).isEqualTo("수정 댓글");
    }

    @DisplayName("회원 댓글은 작성자 본인 여부를 확인할 수 있다.")
    @Test
    void isOwnedBy() {
        //given
        Member member = FixtureUtil.testMentee();
        ReflectionTestUtils.setField(member, "id", 1L);
        Post post = FixtureUtil.testGuestPost();
        Comment comment = Comment.forMember(post, member, "댓글", false, "닉네임", null, null);

        //when //then
        assertThat(comment.isOwnedBy(1L)).isTrue();
        assertThat(comment.isOwnedBy(2L)).isFalse();
    }

    @DisplayName("비회원 댓글은 비밀번호가 일치하면 통과한다.")
    @Test
    void matchGuestPassword() {
        //given
        Post post = FixtureUtil.testGuestPost();
        Comment comment = Comment.forGuest(post, "댓글", "비회원", "1234", null, null);

        //when //then
        assertThatCode(() -> comment.matchGuestPassword("1234"))
                .doesNotThrowAnyException();
    }

    @DisplayName("비회원 댓글은 비밀번호가 일치하지 않으면 예외가 발생한다.")
    @Test
    void failGuestPassword() {
        //given
        Post post = FixtureUtil.testGuestPost();
        Comment comment = Comment.forGuest(post, "댓글", "비회원", "1234", null, null);

        //when //then
        assertThatThrownBy(() -> comment.matchGuestPassword("9999"))
                .isInstanceOf(MisMatchPasswordException.class)
                .hasMessage(BusinessErrorMessage.GUEST_PASSWORD_MISMATCH.getMessage());
    }

    @DisplayName("댓글이 특정 게시글에 속하는지 확인할 수 있다.")
    @Test
    void belongsTo() {
        //given
        Post post = FixtureUtil.testGuestPost();
        ReflectionTestUtils.setField(post, "id", 1L);
        Comment comment = Comment.forGuest(post, "댓글", "비회원", "1234", null, null);

        //when //then
        assertThat(comment.belongsTo(1L)).isTrue();
        assertThat(comment.belongsTo(2L)).isFalse();
    }
}
