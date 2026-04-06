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

class PostTest {

    @DisplayName("회원 게시글을 생성할 수 있다.")
    @Test
    void createMemberPost() {
        //given
        Member member = FixtureUtil.testMentee();

        //when
        Post actual = Post.forMember(member, "제목", "본문", true, "익명");

        //then
        assertThat(actual)
                .extracting(Post::getMember, Post::getTitle, Post::getContent, Post::isAnonymous, Post::getNickname)
                .containsExactly(member, "제목", "본문", true, "익명");
        assertThat(actual.isGuestPost()).isFalse();
    }

    @DisplayName("비회원 게시글을 생성할 수 있다.")
    @Test
    void createGuestPost() {
        //given //when
        Post actual = Post.forGuest("제목", "본문", "비회원", "1234");

        //then
        assertThat(actual.getMember()).isNull();
        assertThat(actual.getNickname()).isEqualTo("비회원");
        assertThat(actual.getGuestPassword()).isNotBlank();
        assertThat(actual.isGuestPost()).isTrue();
    }

    @DisplayName("게시글 제목과 본문을 수정할 수 있다.")
    @Test
    void modify() {
        //given
        Post post = FixtureUtil.testGuestPost();

        //when
        post.modify("수정 제목", "수정 본문");

        //then
        assertThat(post)
                .extracting(Post::getTitle, Post::getContent)
                .containsExactly("수정 제목", "수정 본문");
    }

    @DisplayName("회원 게시글은 작성자 본인 여부를 확인할 수 있다.")
    @Test
    void isOwnedBy() {
        //given
        Member member = FixtureUtil.testMentee();
        ReflectionTestUtils.setField(member, "id", 1L);
        Post post = FixtureUtil.testMemberPost(member);

        //when //then
        assertThat(post.isOwnedBy(1L)).isTrue();
        assertThat(post.isOwnedBy(2L)).isFalse();
    }

    @DisplayName("비회원 게시글은 비밀번호가 일치하면 통과한다.")
    @Test
    void matchGuestPassword() {
        //given
        Post post = FixtureUtil.testGuestPost();

        //when //then
        assertThatCode(() -> post.matchGuestPassword("1234"))
                .doesNotThrowAnyException();
    }

    @DisplayName("비회원 게시글은 비밀번호가 일치하지 않으면 예외가 발생한다.")
    @Test
    void failGuestPassword() {
        //given
        Post post = FixtureUtil.testGuestPost();

        //when //then
        assertThatThrownBy(() -> post.matchGuestPassword("9999"))
                .isInstanceOf(MisMatchPasswordException.class)
                .hasMessage(BusinessErrorMessage.GUEST_PASSWORD_MISMATCH.getMessage());
    }

    @DisplayName("비회원 게시글의 비밀번호가 null 또는 공백이면 예외가 발생한다.")
    @Test
    void failGuestPasswordWhenBlank() {
        //given
        Post post = FixtureUtil.testGuestPost();

        //when //then
        assertThatThrownBy(() -> post.matchGuestPassword(" "))
                .isInstanceOf(MisMatchPasswordException.class)
                .hasMessage(BusinessErrorMessage.GUEST_PASSWORD_MISMATCH.getMessage());
        assertThatThrownBy(() -> post.matchGuestPassword(null))
                .isInstanceOf(MisMatchPasswordException.class)
                .hasMessage(BusinessErrorMessage.GUEST_PASSWORD_MISMATCH.getMessage());
    }
}
