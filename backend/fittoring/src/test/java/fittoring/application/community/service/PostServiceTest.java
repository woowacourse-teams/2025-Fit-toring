package fittoring.application.community.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import fittoring.IntegrationTestSupport;
import fittoring.application.FixtureUtil;
import fittoring.application.community.presentation.dto.response.PostDetailResponse;
import fittoring.application.community.presentation.dto.response.PostListResponse;
import fittoring.application.community.repository.CommentRepository;
import fittoring.application.community.repository.PostRepository;
import fittoring.application.community.service.dto.PostCreateDto;
import fittoring.application.community.service.dto.PostDeleteDto;
import fittoring.application.community.service.dto.PostUpdateDto;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.EmptyRequestException;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.MisMatchPasswordException;
import fittoring.application.exception.PostNotFoundException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.Member;
import fittoring.domain.model.LikeActorKeyHash;
import fittoring.domain.model.Post;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class PostServiceTest extends IntegrationTestSupport {

    private static final LikeActorKeyHash ACTOR_1 = new LikeActorKeyHash("a".repeat(64));
    private static final LikeActorKeyHash ACTOR_2 = new LikeActorKeyHash("b".repeat(64));

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostLikeService postLikeService;

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

    @DisplayName("비회원 게시글 생성 시 닉네임과 비밀번호는 필수다.")
    @Test
    void createGuestPostFailWhenGuestFieldsAreBlank() {
        assertThatThrownBy(() -> postService.createPost(
                new PostCreateDto(null, "title", "content", false, "", "1234")))
                .isInstanceOf(EmptyRequestException.class)
                .hasMessage(BusinessErrorMessage.GUEST_NICKNAME_REQUIRED.getMessage());

        assertThatThrownBy(() -> postService.createPost(
                new PostCreateDto(null, "title", "content", false, "guest", "")))
                .isInstanceOf(EmptyRequestException.class)
                .hasMessage(BusinessErrorMessage.GUEST_PASSWORD_REQUIRED.getMessage());
    }

    @DisplayName("게시글 목록을 커서 기반으로 조회한다.")
    @Test
    void findPosts() {
        for (int i = 0; i < 11; i++) {
            insertPost("title-" + i, "content-" + i, "nick-" + i, LocalDateTime.of(2026, 4, 1, 12, 0).minusMinutes(i));
        }

        PostListResponse firstPage = postService.findPosts(null, null);
        PostListResponse secondPage = postService.findPosts(null, firstPage.nextCursorCode());

        assertSoftly(softly -> {
            softly.assertThat(firstPage.posts()).hasSize(10);
            softly.assertThat(firstPage.hasNext()).isTrue();
            softly.assertThat(firstPage.nextCursorCode()).isNotBlank();
            softly.assertThat(secondPage.posts()).hasSize(1);
        });
    }

    @DisplayName("게시글 제목에 검색어가 포함된 게시글을 조회한다.")
    @Test
    void findPostsByTitleKeyword() {
        insertPost("운동 루틴 질문", "내용", "nick", LocalDateTime.of(2026, 4, 1, 12, 0));
        insertPost("식단 질문", "내용", "nick", LocalDateTime.of(2026, 4, 1, 11, 0));

        PostListResponse actual = postService.findPosts("운동", null);

        assertSoftly(softly -> {
            softly.assertThat(actual.posts()).hasSize(1);
            softly.assertThat(actual.posts().getFirst().title()).isEqualTo("운동 루틴 질문");
            softly.assertThat(actual.hasNext()).isFalse();
            softly.assertThat(actual.nextCursorCode()).isNull();
        });
    }

    @DisplayName("게시글 내용에 검색어가 포함된 게시글을 조회한다.")
    @Test
    void findPostsByContentKeyword() {
        insertPost("내용 검색 대상", "하체 운동 루틴이 궁금합니다.", "nick", LocalDateTime.of(2026, 4, 1, 12, 0));
        insertPost("내용 검색 제외", "식단이 궁금합니다.", "nick", LocalDateTime.of(2026, 4, 1, 11, 0));

        PostListResponse actual = postService.findPosts("하체", null);

        assertSoftly(softly -> {
            softly.assertThat(actual.posts()).hasSize(1);
            softly.assertThat(actual.posts().getFirst().title()).isEqualTo("내용 검색 대상");
            softly.assertThat(actual.hasNext()).isFalse();
            softly.assertThat(actual.nextCursorCode()).isNull();
        });
    }

    @DisplayName("검색어가 공백이면 전체 게시글 목록을 조회한다.")
    @Test
    void findPostsByBlankKeyword() {
        insertPost("운동 루틴 질문", "내용", "nick", LocalDateTime.of(2026, 4, 1, 12, 0));
        insertPost("식단 질문", "내용", "nick", LocalDateTime.of(2026, 4, 1, 11, 0));

        PostListResponse actual = postService.findPosts("   ", null);

        assertThat(actual.posts()).hasSize(2);
    }

    @DisplayName("검색 결과를 커서 기반으로 조회한다.")
    @Test
    void findPostsByKeywordWithCursor() {
        for (int i = 0; i < 11; i++) {
            insertPost("운동 질문-" + i, "content-" + i, "nick-" + i,
                    LocalDateTime.of(2026, 4, 1, 12, 0).minusMinutes(i));
        }
        insertPost("식단 질문", "content", "nick", LocalDateTime.of(2026, 4, 1, 11, 30));

        PostListResponse firstPage = postService.findPosts("운동", null);
        PostListResponse secondPage = postService.findPosts("운동", firstPage.nextCursorCode());

        assertSoftly(softly -> {
            softly.assertThat(firstPage.posts()).hasSize(10);
            softly.assertThat(firstPage.hasNext()).isTrue();
            softly.assertThat(firstPage.nextCursorCode()).isNotBlank();
            softly.assertThat(secondPage.posts()).hasSize(1);
            softly.assertThat(secondPage.hasNext()).isFalse();
        });
    }

    @DisplayName("삭제된 게시글은 검색 결과에서 제외한다.")
    @Test
    void findPostsByKeywordExcludeDeletedPost() {
        Post deletedPost = postRepository.save(Post.forGuest("운동 질문", "삭제된 내용", "nick", "1234"));
        postRepository.delete(deletedPost);
        insertPost("노출되는 운동 질문", "내용", "nick", LocalDateTime.of(2026, 4, 1, 12, 0));

        PostListResponse actual = postService.findPosts("운동", null);

        assertSoftly(softly -> {
            softly.assertThat(actual.posts()).hasSize(1);
            softly.assertThat(actual.posts().getFirst().title()).isEqualTo("노출되는 운동 질문");
        });
    }

    @DisplayName("게시글 목록 조회 시 각 게시글의 댓글 수, 조회수, 좋아요 수가 포함된다.")
    @Test
    void findPostsWithCounts() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        post.increaseViewCount();
        post.increaseViewCount();
        postRepository.save(post);
        commentRepository.save(FixtureUtil.testGuestComment(post));
        commentRepository.save(FixtureUtil.testGuestComment(post));
        commentRepository.save(FixtureUtil.testGuestComment(post));

        // when
        PostListResponse actual = postService.findPosts(null, null);

        // then
        PostListResponse.PostSummary summary = actual.posts().stream()
                .filter(s -> s.id().equals(post.getId()))
                .findFirst()
                .orElseThrow();
        assertSoftly(softly -> {
            softly.assertThat(summary.commentCount()).isEqualTo(3);
            softly.assertThat(summary.viewCount()).isEqualTo(2);
            softly.assertThat(summary.likeCount()).isZero();
        });
    }

    @DisplayName("게시글 상세를 조회한다.")
    @Test
    void findPost() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());

        PostDetailResponse actual = postService.findPost(post.getId(), null);

        assertThat(actual.id()).isEqualTo(post.getId());
    }

    @DisplayName("게시글 상세 조회 시 댓글 수와 조회수가 정상 반영된다.")
    @Test
    void findPostWithCommentCountAndViewCount() {
        // given
        Member member = memberRepository.save(FixtureUtil.testMentee());
        Post post = postRepository.save(FixtureUtil.testMemberPost(member));
        commentRepository.save(FixtureUtil.testGuestComment(post));
        commentRepository.save(FixtureUtil.testGuestComment(post));

        // when
        PostDetailResponse actual = postService.findPost(post.getId(), null);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual.commentCount()).isEqualTo(2);
            softly.assertThat(actual.viewCount()).isEqualTo(1);
            softly.assertThat(actual.likeCount()).isZero();
        });
    }

    @DisplayName("게시글 상세 조회 시 likeActorId가 좋아요한 게시글이면 liked가 true이다.")
    @Test
    void findPostWithLiked() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        postLikeService.like(post.getId(), ACTOR_1);

        // when
        PostDetailResponse liked = postService.findPost(post.getId(), ACTOR_1);
        PostDetailResponse notLiked = postService.findPost(post.getId(), ACTOR_2);

        // then
        assertSoftly(softly -> {
            softly.assertThat(liked.liked()).isTrue();
            softly.assertThat(liked.likeCount()).isEqualTo(1);
            softly.assertThat(notLiked.liked()).isFalse();
            softly.assertThat(notLiked.likeCount()).isEqualTo(1);
        });
    }

    @DisplayName("게시글별 댓글 수를 조회한다.")
    @Test
    void countCommentsByPostId() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Post otherPost = postRepository.save(FixtureUtil.testGuestPost());
        commentRepository.save(FixtureUtil.testGuestComment(post));
        commentRepository.save(FixtureUtil.testGuestComment(post));
        commentRepository.save(FixtureUtil.testGuestComment(otherPost));

        // when
        long actual = commentRepository.countByPostId(post.getId());

        // then
        assertThat(actual).isEqualTo(2);
    }

    @DisplayName("여러 게시글의 댓글을 한 번에 조회한다.")
    @Test
    void findAllByPostIdIn() {
        // given
        Post post1 = postRepository.save(FixtureUtil.testGuestPost());
        Post post2 = postRepository.save(FixtureUtil.testGuestPost());
        Post post3 = postRepository.save(FixtureUtil.testGuestPost());
        commentRepository.save(FixtureUtil.testGuestComment(post1));
        commentRepository.save(FixtureUtil.testGuestComment(post1));
        commentRepository.save(FixtureUtil.testGuestComment(post2));

        // when
        java.util.List<fittoring.domain.model.Comment> actual = commentRepository.findAllByPostIdIn(
                java.util.List.of(post1.getId(), post2.getId(), post3.getId()));

        // then
        assertThat(actual).hasSize(3);
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
        assertThatThrownBy(() -> postService.findPost(999L, null))
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
