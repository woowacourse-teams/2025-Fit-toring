package fittoring.integration;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import fittoring.AbstractApiDocumentationTest;
import fittoring.application.FixtureUtil;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.community.presentation.dto.request.CommentCreateRequest;
import fittoring.application.community.presentation.dto.request.CommentUpdateRequest;
import fittoring.application.community.presentation.dto.request.GuestPasswordRequest;
import fittoring.application.community.presentation.dto.response.CommentResponse;
import fittoring.application.community.repository.CommentRepository;
import fittoring.application.community.repository.PostRepository;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.Comment;
import fittoring.domain.model.Member;
import fittoring.domain.model.Post;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentIntegrationTest extends AbstractApiDocumentationTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @DisplayName("회원 댓글 작성은 닉네임과 비밀번호 없이 201을 반환한다.")
    @Test
    void createMemberCommentWithoutNicknameAndPassword() {
        Member member = memberRepository.save(FixtureUtil.testMentee());
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        CommentCreateRequest request = new CommentCreateRequest("content", false, null, null, null, null);

        CommentResponse response = RestAssured.given(spec)
                .contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(request)
                .when()
                .post("/posts/{postId}/comments", post.getId())
                .then()
                .statusCode(201)
                .extract()
                .as(CommentResponse.class);

        assertThat(response.isGuestComment()).isFalse();
    }

    @DisplayName("비회원 댓글 작성은 201을 반환한다.")
    @Test
    void createComment() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        CommentCreateRequest request = new CommentCreateRequest("content", false, "guest", "1234", null, null);

        CommentResponse response = RestAssured.given(spec)
                .filter(documentWithTag("comment/post-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("댓글")
                                .summary("댓글 작성")
                                .description("게시글에 댓글 또는 대댓글을 작성합니다.")
                                .requestSchema(Schema.schema("CommentCreateRequest"))
                                .responseSchema(Schema.schema("CommentResponse"))
                                .build())))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/posts/{postId}/comments", post.getId())
                .then()
                .statusCode(201)
                .extract()
                .as(CommentResponse.class);

        assertThat(response.content()).isEqualTo("content");
    }

    @DisplayName("비회원 댓글 닉네임이 50자를 초과하면 400을 반환한다.")
    @Test
    void createGuestCommentFailWhenNicknameIsTooLong() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        CommentCreateRequest request = new CommentCreateRequest("content", false, "a".repeat(51), "1234", null, null);

        RestAssured.given(spec)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/posts/{postId}/comments", post.getId())
                .then()
                .statusCode(400);
    }

    @DisplayName("댓글 목록 조회는 200을 반환한다.")
    @Test
    void findComments() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        commentRepository.save(FixtureUtil.testGuestComment(post, "content"));

        List<CommentResponse> response = RestAssured.given(spec)
                .filter(documentWithTag("comment/get-list",
                        resource(ResourceSnippetParameters.builder()
                                .tag("댓글")
                                .summary("댓글 목록 조회")
                                .description("게시글에 등록된 댓글과 대댓글 목록을 조회합니다.")
                                .responseSchema(Schema.schema("CommentListResponse"))
                                .build())))
                .when()
                .get("/posts/{postId}/comments", post.getId())
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {});

        assertThat(response).hasSize(1);
    }

    @DisplayName("댓글 좋아요는 postLikeActorId 쿠키 기준으로 한 번만 증가한다.")
    @Test
    void likeComment() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(FixtureUtil.testGuestComment(post));

        // when
        Response first = RestAssured.given(spec)
                .filter(documentWithTag("comment/put-like",
                        resource(ResourceSnippetParameters.builder()
                                .tag("댓글")
                                .summary("댓글 좋아요")
                                .description("postLikeActorId 쿠키 기준으로 댓글 또는 대댓글 좋아요를 추가합니다.")
                                .responseSchema(Schema.schema("CommentLikeResponse"))
                                .build())))
                .when()
                .post("/posts/{postId}/comments/{commentId}/like", post.getId(), comment.getId())
                .then()
                .statusCode(200)
                .extract()
                .response();

        String postLikeActorId = first.cookie("postLikeActorId");
        Response second = RestAssured.given(spec)
                .cookie("postLikeActorId", postLikeActorId)
                .when()
                .post("/posts/{postId}/comments/{commentId}/like", post.getId(), comment.getId())
                .then()
                .statusCode(200)
                .extract()
                .response();

        // then
        assertSoftly(softly -> {
            softly.assertThat(postLikeActorId).isNotBlank();
            softly.assertThat(first.jsonPath().getLong("commentId")).isEqualTo(comment.getId());
            softly.assertThat(first.jsonPath().getBoolean("liked")).isTrue();
            softly.assertThat(first.jsonPath().getInt("likeCount")).isEqualTo(1);
            softly.assertThat(second.jsonPath().getBoolean("liked")).isTrue();
            softly.assertThat(second.jsonPath().getInt("likeCount")).isEqualTo(1);
        });
    }

    @DisplayName("댓글 좋아요 취소는 postLikeActorId 쿠키 기준으로 한 번만 감소한다.")
    @Test
    void unlikeComment() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(FixtureUtil.testGuestComment(post));
        Response likeResponse = RestAssured.given(spec)
                .when()
                .post("/posts/{postId}/comments/{commentId}/like", post.getId(), comment.getId())
                .then()
                .statusCode(200)
                .extract()
                .response();
        String postLikeActorId = likeResponse.cookie("postLikeActorId");

        // when
        Response first = RestAssured.given(spec)
                .filter(documentWithTag("comment/delete-like",
                        resource(ResourceSnippetParameters.builder()
                                .tag("댓글")
                                .summary("댓글 좋아요 취소")
                                .description("postLikeActorId 쿠키 기준으로 댓글 또는 대댓글 좋아요를 취소합니다.")
                                .responseSchema(Schema.schema("CommentLikeResponse"))
                                .build())))
                .cookie("postLikeActorId", postLikeActorId)
                .when()
                .delete("/posts/{postId}/comments/{commentId}/like", post.getId(), comment.getId())
                .then()
                .statusCode(200)
                .extract()
                .response();

        Response second = RestAssured.given(spec)
                .cookie("postLikeActorId", postLikeActorId)
                .when()
                .delete("/posts/{postId}/comments/{commentId}/like", post.getId(), comment.getId())
                .then()
                .statusCode(200)
                .extract()
                .response();

        // then
        assertSoftly(softly -> {
            softly.assertThat(first.jsonPath().getLong("commentId")).isEqualTo(comment.getId());
            softly.assertThat(first.jsonPath().getBoolean("liked")).isFalse();
            softly.assertThat(first.jsonPath().getInt("likeCount")).isZero();
            softly.assertThat(second.jsonPath().getBoolean("liked")).isFalse();
            softly.assertThat(second.jsonPath().getInt("likeCount")).isZero();
        });
    }

    @DisplayName("댓글 목록 조회는 postLikeActorId 쿠키 기준 liked를 반환한다.")
    @Test
    void findCommentsWithLiked() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(FixtureUtil.testGuestComment(post));
        Response likeResponse = RestAssured.given(spec)
                .when()
                .post("/posts/{postId}/comments/{commentId}/like", post.getId(), comment.getId())
                .then()
                .statusCode(200)
                .extract()
                .response();
        String postLikeActorId = likeResponse.cookie("postLikeActorId");

        // when
        List<CommentResponse> responses = RestAssured.given(spec)
                .cookie("postLikeActorId", postLikeActorId)
                .when()
                .get("/posts/{postId}/comments", post.getId())
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {});

        // then
        CommentResponse actual = responses.get(0);
        assertSoftly(softly -> {
            softly.assertThat(actual.id()).isEqualTo(comment.getId());
            softly.assertThat(actual.likeCount()).isEqualTo(1);
            softly.assertThat(actual.liked()).isTrue();
        });
    }

    @DisplayName("댓글 수정은 200을 반환한다.")
    @Test
    void modifyComment() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(FixtureUtil.testGuestComment(post, "old"));
        CommentUpdateRequest request = new CommentUpdateRequest("new", "1234");

        RestAssured.given(spec)
                .filter(documentWithTag("comment/patch-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("댓글")
                                .summary("댓글 수정")
                                .description("댓글 또는 대댓글 내용을 수정합니다.")
                                .requestSchema(Schema.schema("CommentUpdateRequest"))
                                .build())))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .patch("/comments/{commentId}", comment.getId())
                .then()
                .statusCode(200);
    }

    @DisplayName("댓글 삭제는 204를 반환한다.")
    @Test
    void deleteComment() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(FixtureUtil.testGuestComment(post, "old"));
        GuestPasswordRequest request = new GuestPasswordRequest("1234");

        RestAssured.given(spec)
                .filter(documentWithTag("comment/delete-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("댓글")
                                .summary("댓글 삭제")
                                .description("댓글 또는 대댓글을 삭제합니다.")
                                .requestSchema(Schema.schema("GuestPasswordRequest"))
                                .build())))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .delete("/comments/{commentId}", comment.getId())
                .then()
                .statusCode(204);
    }

    @DisplayName("비회원 댓글 비밀번호 확인은 200을 반환한다.")
    @Test
    void validateGuestPassword() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(FixtureUtil.testGuestComment(post));
        GuestPasswordRequest request = new GuestPasswordRequest("1234");

        RestAssured.given(spec)
                .filter(documentWithTag("comment/pw-check-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("댓글")
                                .summary("비회원 댓글 비밀번호 확인")
                                .description("비회원 댓글의 비밀번호를 확인합니다.")
                                .requestSchema(Schema.schema("GuestPasswordRequest"))
                                .build())))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/comments/{commentId}/pw-check", comment.getId())
                .then()
                .statusCode(200);
    }
}
