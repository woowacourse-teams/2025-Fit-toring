package fittoring.application.community.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import fittoring.IntegrationTestSupport;
import fittoring.application.FixtureUtil;
import fittoring.application.community.presentation.dto.response.PostDetailResponse;
import fittoring.application.community.presentation.dto.response.PostListResponse;
import fittoring.application.community.repository.PostRepository;
import fittoring.application.community.service.dto.PostCreateDto;
import fittoring.application.community.service.dto.PostDeleteDto;
import fittoring.application.community.service.dto.PostUpdateDto;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.MisMatchPasswordException;
import fittoring.application.exception.PostNotFoundException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.Member;
import fittoring.domain.model.Post;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class PostServiceTest extends IntegrationTestSupport {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DisplayName("회원 게시글을 생성한다.")
    @Test
    void createMemberPost() {
        Member member = memberRepository.save(FixtureUtil.testMentee());
        PostCreateDto dto = new PostCreateDto(member.getId(), "title", "content", false, null, null);

        PostDetailResponse actual = postService.createPost(dto);

        assertSoftly(softly -> {
            softly.assertThat(actual.title()).isEqualTo("title");
            softly.assertThat(actual.content()).isEqualTo("content");
            softly.assertThat(actual.nickname()).isEqualTo(member.getName());
            softly.assertThat(actual.isGuestPost()).isFalse();
        });
    }

    @DisplayName("비회원 게시글을 생성한다.")
    @Test
    void createGuestPost() {
        PostCreateDto dto = new PostCreateDto(null, "title", "content", false, "guest", "1234");

        PostDetailResponse actual = postService.createPost(dto);

        assertSoftly(softly -> {
            softly.assertThat(actual.title()).isEqualTo("title");
            softly.assertThat(actual.nickname()).isEqualTo("guest");
            softly.assertThat(actual.isGuestPost()).isTrue();
        });
    }

    @DisplayName("게시글 목록을 커서 기반으로 조회한다.")
    @Test
    void findPosts() {
        for (int i = 0; i < 11; i++) {
            insertPost("title-" + i, "content-" + i, "nick-" + i, LocalDateTime.of(2026, 4, 1, 12, 0).minusMinutes(i));
        }

        PostListResponse firstPage = postService.findPosts(null);
        PostListResponse secondPage = postService.findPosts(firstPage.nextCursorCode());

        assertSoftly(softly -> {
            softly.assertThat(firstPage.posts()).hasSize(10);
            softly.assertThat(firstPage.hasNext()).isTrue();
            softly.assertThat(firstPage.nextCursorCode()).isNotBlank();
            softly.assertThat(secondPage.posts()).hasSize(1);
        });
    }

    @DisplayName("게시글 상세를 조회한다.")
    @Test
    void findPost() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());

        PostDetailResponse actual = postService.findPost(post.getId());

        assertThat(actual.id()).isEqualTo(post.getId());
    }

    @DisplayName("회원 게시글을 수정한다.")
    @Test
    void modifyMemberPost() {
        Member member = memberRepository.save(FixtureUtil.testMentee());
        Post post = postRepository.save(FixtureUtil.testMemberPost(member));

        postService.modifyPost(new PostUpdateDto(member.getId(), post.getId(), "new-title", "new-content", null));

        Post updated = postRepository.findById(post.getId()).orElseThrow();
        assertSoftly(softly -> {
            softly.assertThat(updated.getTitle()).isEqualTo("new-title");
            softly.assertThat(updated.getContent()).isEqualTo("new-content");
        });
    }

    @DisplayName("비회원 게시글을 비밀번호로 수정한다.")
    @Test
    void modifyGuestPost() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());

        postService.modifyPost(new PostUpdateDto(null, post.getId(), "new-title", "new-content", "1234"));

        Post updated = postRepository.findById(post.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("new-title");
    }

    @DisplayName("다른 회원은 회원 게시글을 수정할 수 없다.")
    @Test
    void modifyMemberPostForbidden() {
        Member owner = memberRepository.save(FixtureUtil.testMentee());
        Member other = memberRepository.save(FixtureUtil.testMentee(2));
        Post post = postRepository.save(FixtureUtil.testMemberPost(owner));

        assertThatThrownBy(() -> postService.modifyPost(
                new PostUpdateDto(other.getId(), post.getId(), "new-title", "new-content", null)))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
    }

    @DisplayName("비회원 게시글의 비밀번호가 틀리면 수정할 수 없다.")
    @Test
    void modifyGuestPostFail() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());

        assertThatThrownBy(() -> postService.modifyPost(
                new PostUpdateDto(null, post.getId(), "new-title", "new-content", "9999")))
                .isInstanceOf(MisMatchPasswordException.class)
                .hasMessage(BusinessErrorMessage.GUEST_PASSWORD_MISMATCH.getMessage());
    }

    @DisplayName("회원 게시글을 삭제하면 soft delete 된다.")
    @Test
    void deleteMemberPost() {
        Member member = memberRepository.save(FixtureUtil.testMentee());
        Post post = postRepository.save(FixtureUtil.testMemberPost(member));

        postService.deletePost(new PostDeleteDto(member.getId(), post.getId(), null));

        assertThat(postRepository.findById(post.getId())).isEmpty();
        assertThat(postRepository.findDeletedById(post.getId()).isDeleted()).isTrue();
    }

    @DisplayName("비회원 게시글 비밀번호를 검증한다.")
    @Test
    void validateGuestPassword() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());

        postService.validateGuestPassword(post.getId(), "1234");

        assertThat(post.getId()).isNotNull();
    }

    @DisplayName("없는 게시글을 조회하면 예외가 발생한다.")
    @Test
    void findPostFail() {
        assertThatThrownBy(() -> postService.findPost(999L))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage(BusinessErrorMessage.POST_NOT_FOUND.getMessage());
    }

    private void insertPost(String title, String content, String nickname, LocalDateTime createdAt) {
        jdbcTemplate.update(
                "INSERT INTO post (title, content, member_id, nickname, guest_password, is_anonymous, created_at, is_deleted, deleted_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                title,
                content,
                null,
                nickname,
                "encrypted",
                false,
                Timestamp.valueOf(createdAt),
                false,
                null
        );
    }
}
