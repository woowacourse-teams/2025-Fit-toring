package fittoring.integration;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.assertj.core.api.Assertions.assertThat;

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
import fittoring.application.community.repository.PostRepository;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.Member;
import fittoring.domain.model.Post;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PostIntegrationTest extends AbstractApiDocumentationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

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
                                .tag("post")
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

        assertThat(response.title()).isEqualTo("title");
    }

    @DisplayName("비회원 게시글 작성은 201을 반환한다.")
    @Test
    void createGuestPost() {
        PostCreateRequest request = new PostCreateRequest("title", "content", false, "guest", "1234");

        PostDetailResponse response = RestAssured.given(spec)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .extract()
                .as(PostDetailResponse.class);

        assertThat(response.isGuestPost()).isTrue();
    }

    @DisplayName("게시글 목록 조회는 200을 반환한다.")
    @Test
    void findPosts() {
        postRepository.save(FixtureUtil.testGuestPost());

        PostListResponse response = RestAssured.given(spec)
                .filter(documentWithTag("post/get-list",
                        resource(ResourceSnippetParameters.builder()
                                .tag("post")
                                .responseSchema(Schema.schema("PostListResponse"))
                                .build())))
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .extract()
                .as(PostListResponse.class);

        assertThat(response.posts()).hasSize(1);
    }

    @DisplayName("게시글 상세 조회는 200을 반환한다.")
    @Test
    void findPost() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());

        PostDetailResponse response = RestAssured.given(spec)
                .filter(documentWithTag("post/get-detail",
                        resource(ResourceSnippetParameters.builder()
                                .tag("post")
                                .responseSchema(Schema.schema("PostDetailResponse"))
                                .build())))
                .when()
                .get("/posts/{postId}", post.getId())
                .then()
                .statusCode(200)
                .extract()
                .as(PostDetailResponse.class);

        assertThat(response.id()).isEqualTo(post.getId());
    }

    @DisplayName("비회원 게시글 수정은 200을 반환한다.")
    @Test
    void modifyPost() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        PostUpdateRequest request = new PostUpdateRequest("new-title", "new-content", "1234");

        RestAssured.given(spec)
                .filter(documentWithTag("post/patch-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("post")
                                .requestSchema(Schema.schema("PostUpdateRequest"))
                                .build())))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .patch("/posts/{postId}", post.getId())
                .then()
                .statusCode(200);
    }

    @DisplayName("비회원 게시글 삭제는 204를 반환한다.")
    @Test
    void deletePost() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        GuestPasswordRequest request = new GuestPasswordRequest("1234");

        RestAssured.given(spec)
                .filter(documentWithTag("post/delete-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("post")
                                .requestSchema(Schema.schema("GuestPasswordRequest"))
                                .build())))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .delete("/posts/{postId}", post.getId())
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
                                .tag("post")
                                .requestSchema(Schema.schema("GuestPasswordRequest"))
                                .build())))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/posts/{postId}/guest-check", post.getId())
                .then()
                .statusCode(200);
    }
}
