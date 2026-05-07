package fittoring.integration;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import fittoring.AbstractApiDocumentationTest;
import fittoring.application.FixtureUtil;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.community.presentation.dto.request.GuestPasswordRequest;
import fittoring.application.community.presentation.dto.request.PostCreateRequest;
import fittoring.application.community.presentation.dto.request.PostUpdateRequest;
import fittoring.application.community.presentation.dto.response.PostDetailResponse;
import fittoring.application.community.presentation.dto.response.PostListResponse;
import fittoring.application.community.repository.CommentRepository;
import fittoring.application.community.repository.PostRepository;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.Member;
import fittoring.domain.model.Post;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PostIntegrationTest extends AbstractApiDocumentationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @DisplayName("회원 게시글 작성은 201을 반환한다.")
    @Test
    void createMemberPost() {
        Member member = memberRepository.save(FixtureUtil.testMentee());
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());
        PostCreateRequest request = new PostCreateRequest("title", "content", false, null, null);

        PostDetailResponse response = RestAssured.given(spec)
                .filter(documentWithTag("post/post-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("게시글")
                                .summary("게시글 작성")
                                .description("회원 또는 비회원이 게시글을 작성합니다.")
                                .requestSchema(Schema.schema("PostCreateRequest"))
                                .responseSchema(Schema.schema("PostDetailResponse"))
                                .build())))
                .contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(request)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .extract()
                .as(PostDetailResponse.class);

        assertSoftly(softly -> {
            softly.assertThat(response.title()).isEqualTo("title");
            softly.assertThat(response.commentCount()).isZero();
            softly.assertThat(response.viewCount()).isZero();
            softly.assertThat(response.likeCount()).isZero();
        });
    }

    @DisplayName("비회원 게시글 작성은 201을 반환한다.")
    @Test
    void createGuestPost() {
        PostCreateRequest request = new PostCreateRequest("title", "content", false, "guest", "1234");

        PostDetailResponse response = RestAssured.given(spec)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/guest/posts")
                .then()
                .statusCode(201)
                .extract()
                .as(PostDetailResponse.class);

        assertThat(response.isGuestPost()).isTrue();
    }

    @DisplayName("게시글 제목이 255자를 초과하면 400을 반환한다.")
    @Test
    void createPostFailWhenTitleIsTooLong() {
        PostCreateRequest request = new PostCreateRequest("a".repeat(256), "content", false, "guest", "1234");

        RestAssured.given(spec)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/guest/posts")
                .then()
                .statusCode(400);
    }

    @DisplayName("비회원 게시글 닉네임이 50자를 초과하면 400을 반환한다.")
    @Test
    void createGuestPostFailWhenNicknameIsTooLong() {
        PostCreateRequest request = new PostCreateRequest("title", "content", false, "a".repeat(51), "1234");

        RestAssured.given(spec)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/guest/posts")
                .then()
                .statusCode(400);
    }

    @DisplayName("게시글 목록 조회는 200을 반환한다.")
    @Test
    void findPosts() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        commentRepository.save(FixtureUtil.testGuestComment(post));

        PostListResponse response = RestAssured.given(spec)
                .filter(documentWithTag("post/get-list",
                        resource(ResourceSnippetParameters.builder()
                                .tag("게시글")
                                .summary("게시글 목록 조회")
                                .description("게시글 목록을 커서 기반으로 조회합니다.")
                                .responseSchema(Schema.schema("PostListResponse"))
                                .build())))
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .extract()
                .as(PostListResponse.class);

        assertThat(response.posts()).hasSize(1);
        PostListResponse.PostSummary summary = response.posts().get(0);
        assertSoftly(softly -> {
            softly.assertThat(summary.commentCount()).isEqualTo(1);
            softly.assertThat(summary.viewCount()).isZero();
            softly.assertThat(summary.likeCount()).isZero();
        });
    }

    @DisplayName("게시글 상세 조회는 200을 반환한다.")
    @Test
    void findPost() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        commentRepository.save(FixtureUtil.testGuestComment(post));

        PostDetailResponse response = RestAssured.given(spec)
                .filter(documentWithTag("post/get-detail",
                        resource(ResourceSnippetParameters.builder()
                                .tag("게시글")
                                .summary("게시글 상세 조회")
                                .description("특정 게시글의 상세 정보를 조회합니다.")
                                .responseSchema(Schema.schema("PostDetailResponse"))
                                .build())))
                .when()
                .get("/posts/{postId}", post.getId())
                .then()
                .statusCode(200)
                .extract()
                .as(PostDetailResponse.class);

        assertSoftly(softly -> {
            softly.assertThat(response.id()).isEqualTo(post.getId());
            softly.assertThat(response.commentCount()).isEqualTo(1);
            softly.assertThat(response.viewCount()).isEqualTo(1);
            softly.assertThat(response.likeCount()).isZero();
        });
    }

    @DisplayName("비회원 게시글 수정은 200을 반환한다.")
    @Test
    void modifyPost() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        PostUpdateRequest request = new PostUpdateRequest("new-title", "new-content", "1234");

        RestAssured.given(spec)
                .filter(documentWithTag("post/patch-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("게시글")
                                .summary("게시글 수정")
                                .description("게시글 제목과 본문을 수정합니다.")
                                .requestSchema(Schema.schema("PostUpdateRequest"))
                                .build())))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .patch("/guest/posts/{postId}", post.getId())
                .then()
                .statusCode(200);

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertThat(updatedPost.getTitle()).isEqualTo("new-title");
        assertThat(updatedPost.getContent()).isEqualTo("new-content");
    }

    @DisplayName("비회원 게시글 수정 시 제목만 변경하면 본문은 유지된다.")
    @Test
    void modifyPostTitleOnly() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        PostUpdateRequest request = new PostUpdateRequest("new-title", null, "1234");

        RestAssured.given(spec)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .patch("/guest/posts/{postId}", post.getId())
                .then()
                .statusCode(200);

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertThat(updatedPost.getTitle()).isEqualTo("new-title");
        assertThat(updatedPost.getContent()).isEqualTo("게시글 본문");
    }

    @DisplayName("비회원 게시글 수정 시 본문만 변경하면 제목은 유지된다.")
    @Test
    void modifyPostContentOnly() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        PostUpdateRequest request = new PostUpdateRequest(null, "new-content", "1234");

        RestAssured.given(spec)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .patch("/guest/posts/{postId}", post.getId())
                .then()
                .statusCode(200);

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertThat(updatedPost.getTitle()).isEqualTo("게시글 제목");
        assertThat(updatedPost.getContent()).isEqualTo("new-content");
    }

    @DisplayName("비회원 게시글 삭제는 204를 반환한다.")
    @Test
    void deletePost() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        GuestPasswordRequest request = new GuestPasswordRequest("1234");

        RestAssured.given(spec)
                .filter(documentWithTag("post/delete-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("게시글")
                                .summary("게시글 삭제")
                                .description("게시글을 삭제합니다.")
                                .requestSchema(Schema.schema("GuestPasswordRequest"))
                                .build())))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .delete("/guest/posts/{postId}", post.getId())
                .then()
                .statusCode(204);
    }

    @DisplayName("비회원 게시글 비밀번호 확인은 200을 반환한다.")
    @Test
    void validateGuestPassword() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        GuestPasswordRequest request = new GuestPasswordRequest("1234");

        RestAssured.given(spec)
                .filter(documentWithTag("post/post-guest-check",
                        resource(ResourceSnippetParameters.builder()
                                .tag("게시글")
                                .summary("비회원 비밀번호 확인")
                                .description("비회원 게시글의 비밀번호를 확인합니다.")
                                .requestSchema(Schema.schema("GuestPasswordRequest"))
                                .build())))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/posts/{postId}/guest-check", post.getId())
                .then()
                .statusCode(200);
    }

    @DisplayName("게시글 좋아요는 likeActorId 쿠키 기준으로 한 번만 증가한다.")
    @Test
    void likePost() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());

        // when
        Response first = RestAssured.given(spec)
                .filter(documentWithTag("post/put-like",
                        resource(ResourceSnippetParameters.builder()
                                .tag("게시글")
                                .summary("게시글 좋아요")
                                .description("likeActorId 쿠키 기준으로 게시글 좋아요를 추가합니다.")
                                .responseSchema(Schema.schema("PostLikeResponse"))
                                .build())))
                .when()
                .post("/posts/{postId}/like", post.getId())
                .then()
                .statusCode(201)
                .extract()
                .response();

        String likeActorId = first.cookie("likeActorId");
        Response second = RestAssured.given(spec)
                .cookie("likeActorId", likeActorId)
                .when()
                .post("/posts/{postId}/like", post.getId())
                .then()
                .statusCode(201)
                .extract()
                .response();

        // then
        assertSoftly(softly -> {
            softly.assertThat(likeActorId).isNotBlank();
            softly.assertThat(first.jsonPath().getLong("postId")).isEqualTo(post.getId());
            softly.assertThat(first.jsonPath().getBoolean("liked")).isTrue();
            softly.assertThat(first.jsonPath().getInt("likeCount")).isEqualTo(1);
            softly.assertThat(second.jsonPath().getBoolean("liked")).isTrue();
            softly.assertThat(second.jsonPath().getInt("likeCount")).isEqualTo(1);
        });
    }

    @DisplayName("게시글 좋아요 취소는 likeActorId 쿠키 기준으로 한 번만 감소한다.")
    @Test
    void unlikePost() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Response likeResponse = RestAssured.given(spec)
                .when()
                .post("/posts/{postId}/like", post.getId())
                .then()
                .statusCode(201)
                .extract()
                .response();
        String likeActorId = likeResponse.cookie("likeActorId");

        // when
        Response first = RestAssured.given(spec)
                .filter(documentWithTag("post/delete-like",
                        resource(ResourceSnippetParameters.builder()
                                .tag("게시글")
                                .summary("게시글 좋아요 취소")
                                .description("likeActorId 쿠키 기준으로 게시글 좋아요를 취소합니다.")
                                .responseSchema(Schema.schema("PostLikeResponse"))
                                .build())))
                .cookie("likeActorId", likeActorId)
                .when()
                .delete("/posts/{postId}/like", post.getId())
                .then()
                .statusCode(200)
                .extract()
                .response();

        Response second = RestAssured.given(spec)
                .cookie("likeActorId", likeActorId)
                .when()
                .delete("/posts/{postId}/like", post.getId())
                .then()
                .statusCode(200)
                .extract()
                .response();

        // then
        assertSoftly(softly -> {
            softly.assertThat(likeActorId).isNotBlank();
            softly.assertThat(first.jsonPath().getLong("postId")).isEqualTo(post.getId());
            softly.assertThat(first.jsonPath().getBoolean("liked")).isFalse();
            softly.assertThat(first.jsonPath().getInt("likeCount")).isZero();
            softly.assertThat(second.jsonPath().getBoolean("liked")).isFalse();
            softly.assertThat(second.jsonPath().getInt("likeCount")).isZero();
        });
    }

    @DisplayName("쿠키 없는 게시글 좋아요 취소는 쿠키를 발급하지 않고 좋아요 수를 변경하지 않는다.")
    @Test
    void unlikePostWithoutCookie() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        RestAssured.given(spec)
                .when()
                .post("/posts/{postId}/like", post.getId())
                .then()
                .statusCode(201);

        // when
        Response response = RestAssured.given(spec)
                .when()
                .delete("/posts/{postId}/like", post.getId())
                .then()
                .statusCode(200)
                .extract()
                .response();

        // then
        assertSoftly(softly -> {
            softly.assertThat(response.cookie("likeActorId")).isNull();
            softly.assertThat(response.jsonPath().getLong("postId")).isEqualTo(post.getId());
            softly.assertThat(response.jsonPath().getBoolean("liked")).isFalse();
            softly.assertThat(response.jsonPath().getInt("likeCount")).isEqualTo(1);
        });
    }

    @DisplayName("게시글 상세 조회는 likeActorId 쿠키 기준 liked를 반환한다.")
    @Test
    void findPostWithLiked() {
        // given
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Response likeResponse = RestAssured.given(spec)
                .when()
                .post("/posts/{postId}/like", post.getId())
                .then()
                .statusCode(201)
                .extract()
                .response();
        String likeActorId = likeResponse.cookie("likeActorId");

        // when
        Response likedDetail = RestAssured.given(spec)
                .cookie("likeActorId", likeActorId)
                .when()
                .get("/posts/{postId}", post.getId())
                .then()
                .statusCode(200)
                .extract()
                .response();

        Response notLikedDetail = RestAssured.given(spec)
                .when()
                .get("/posts/{postId}", post.getId())
                .then()
                .statusCode(200)
                .extract()
                .response();

        // then
        assertSoftly(softly -> {
            softly.assertThat(likedDetail.jsonPath().getLong("id")).isEqualTo(post.getId());
            softly.assertThat(likedDetail.jsonPath().getBoolean("liked")).isTrue();
            softly.assertThat(likedDetail.jsonPath().getInt("likeCount")).isEqualTo(1);
            softly.assertThat(notLikedDetail.jsonPath().getBoolean("liked")).isFalse();
            softly.assertThat(notLikedDetail.jsonPath().getInt("likeCount")).isEqualTo(1);
        });
    }
}
